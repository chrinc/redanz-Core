package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AccommodationService {

  private final SlotService slotService;
  private final SleepUtilService sleepUtilService;
  private final FoodService foodService;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;
  private final EventTypeSlotService eventTypeSlotService;

  public AccommodationResponse getResponse(Event event) {
    return new AccommodationResponse(
      slotService.getAccommodationSlots(event),
      slotService.getAccommodationSlots(event),
      foodService.getFoodSlots(event),
      sleepUtilService.findHostSleepUtils(),
      sleepUtilService.findHosteeSleepUtils()
    );
  }



  public Field getField(String key) {
    Field field;
    try {
      field = Event.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateHosting(JsonObject request, Event event) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Set<Slot> newHostingDays = new HashSet<>();

    getHostingSchema(event).forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;
        try {
          switch(type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getField(key);
                  field.set(event, outTextKey);
                }
              }
              break;
            case "text":
              field = getField(key);

              field.set(event, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "date":
              field = getField(key);

              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(event, request.get(key).isJsonNull() ? null : localDate);
              break;

            case "datetime":
              field = getField(key);
              // registrationStart":{"date":"2023-07-29","time":"23:00"}
              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
              String dateTimeDateString = request.get(key).getAsJsonObject().get("date").getAsString().substring(0, 10);
              String dateTimeTimeString = request.get(key).getAsJsonObject().get("time").isJsonNull() ? "12:00" : request.get(key).getAsJsonObject().get("time").getAsString();
              ZoneId zoneId = ZoneId.of("Europe/Zurich");
              LocalDateTime dateTime = LocalDateTime.parse(dateTimeDateString + " " + dateTimeTimeString, dateTimeFormatter);

              // hack hack hack, need fix plus Days
              ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId).plusDays(1);
              field.set(event, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "multiselect":

              if (request.get(key) != null && request.get(key).isJsonArray()) {
                request.get(key).getAsJsonArray().forEach(item -> {
                  newHostingDays.add(slotService.findBySlotId(item.getAsLong()));
                });
              }
              break;


            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    eventRepo.save(event);

    List<Slot> existingHostingDays
      = eventTypeSlotService.findByEventAndType(event, "accommodation").stream()
      .map(EventTypeSlot::getTypeSlot)
      .map(TypeSlot::getSlot)
      .filter(Objects::nonNull) // Filter out any null slots, if necessary
      .collect(Collectors.toList());

    // Save new discounts
    newHostingDays.stream()
      .filter(hostingDay -> !existingHostingDays.contains(hostingDay))
      .forEach(hostingDay -> slotService.save(new EventTypeSlot(slotService.findByTypeAndSlot("accommodation", hostingDay), event, hostingDay.getSeqNr())));

    // Remove existing discounts not in new discounts
    existingHostingDays.stream()
      .filter(existingHostingDay -> !newHostingDays.contains(existingHostingDay))
      .forEach(existingHostingDay -> {
        EventTypeSlot eventTypeSlot = eventTypeSlotService.findByEventAndTypeSlot(event, slotService.findByTypeAndSlot("accommodation", existingHostingDay));
        eventTypeSlotService.delete(eventTypeSlot);
      });
  }

  public void deleteHosting(JsonObject request, Event event) {
    event.setHosting(false);
    eventRepo.save(event);
    eventTypeSlotService.deleteByEventAndType(event, "accommodation");
  }

  public List<Map<String, String>> getHostingSchema(Event event) {
      return new ArrayList<>() {
        {
          add(new HashMap<>() {{put("key", "id");            put("type", "id");             put("label", "id");}});
          add(new HashMap<>() {{put("key", "hosting");       put("type", "bool");                                   put("labelTrue", "Enable Hosting"); put("labelFalse", "Disable Hosting");}});
          add(new HashMap<>() {{put("key", "hostingDays");   put("type", "multiselect");  put("required", "false"); put("label", "Hosting Days");  put("list", getHostingDays().toString());}});
          add(new HashMap<>() {{put("key", "count");         put("type", "single");}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "accommodation");  put("label", OutTextConfig.LABEL_ACCOMMODATION_INFO_EN.getOutTextKey());}});
//          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "host");           put("label", OutTextConfig.LABEL_HOST_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hostYes");        put("label", OutTextConfig.LABEL_HOSTYES_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hostCount");      put("label", OutTextConfig.LABEL_HOSTCOUNT_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hostDays");       put("label", OutTextConfig.LABEL_HOSTDAYS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hostSpots");      put("label", OutTextConfig.LABEL_HOSTSPOTS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hostComment");    put("label", OutTextConfig.LABEL_HOSTCOMMENT_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeYes");    put("label", OutTextConfig.LABEL_HOSTEEYES_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeShare");    put("label", OutTextConfig.LABEL_HOSTEESHARE_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeShareBeds");    put("label", OutTextConfig.LABEL_HOSTEESHAREBEDS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeSharePerson");    put("label", OutTextConfig.LABEL_HOSTEESHAREPERSON_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeDays");    put("label", OutTextConfig.LABEL_HOSTEEDAYS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeSpots");    put("label", OutTextConfig.LABEL_HOSTEESPOTS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "hosteeComments");    put("label", OutTextConfig.LABEL_HOSTEECOMMENTS_INFO_EN.getOutTextKey());}});
          add(new HashMap<>() {{put("key", "plural");         put("type", "title");           put("label", OutTextConfig.LABEL_ACCOMMODATION_EN.getOutTextKey()); }});
          add(new HashMap<>() {{put("key", "singular");         put("type", "title");           put("label", OutTextConfig.LABEL_ACCOMMODATION_EN.getOutTextKey()); }});

        }
      };
  }

  public List<Map<String, String>> getHostingDays() {
    return slotService.findAllByType("accommodation")
      .stream()
      .map(typeSlot -> typeSlot.getSlot().dataMap())
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getHostingData(List<Event> eventList) {
    List<Map<String, String>> hostingData = new ArrayList<>();
    eventList.forEach(event -> {

       // discount data
       Map<String, String> hostingMap = dataMap(event);
       hostingData.add(hostingMap);
    });

    return hostingData;
  }

  public List<EventTypeSlot> eventHostingList (Event event) {
    return eventTypeSlotService.findByEventAndType(event, "accommodation");
  }

  public Map<String, String> dataMap(Event event) {
    return new HashMap<>() {
      {
        put("id", String.valueOf(event.getEventId()));
        put("hosting", String.valueOf(event.isHosting()));
        put("hostingDays", slotService.getAllSlots("accommodation", event).stream().map(slot -> slot.getSlotId()).collect(Collectors.toList()).toString());
      }
    };
  }
}
