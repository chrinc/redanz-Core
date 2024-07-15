package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.VolunteerTypeRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class VolunteeringService {
  private final OutTextService outTextService;
  private final EventTypeSlotService eventTypeSlotService;
  private final EventRepo eventRepo;
  private final SlotService slotService;
  private final VolunteerTypeRepo volunteerTypeRepo;

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

  public void updateVolunteering(JsonObject request, Event event) throws IOException, TemplateException {
    Set<Slot> newVolunteerSlots = new HashSet<>();
    List<VolunteerType> newVolunteerTypes = new ArrayList<>();

    getVolunteerSchema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch(type) {
            case "bool":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "multiselect":
              if (request.get(key) != null && request.get(key).isJsonArray()) {

                switch(key) {
                  case "volunteerSlots":
                    request.get(key).getAsJsonArray().forEach(item -> {
                      newVolunteerSlots.add(slotService.findBySlotId(item.getAsLong()));
                    });
                    break;
                  case "volunteerTypes":
                    request.get(key).getAsJsonArray().forEach(item -> {
                      newVolunteerTypes.add(volunteerTypeRepo.findByVolunteerTypeId(item.getAsLong()));
                    });
                    break;
                }
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

    event.setVolunteerTypes(newVolunteerTypes);
    eventRepo.save(event);

    List<Slot> existingVolunteerSlots
      = eventTypeSlotService.findByEventAndType(event, "volunteer").stream()
      .map(EventTypeSlot::getTypeSlot)
      .map(TypeSlot::getSlot)
      .filter(Objects::nonNull) // Filter out any null slots, if necessary
      .collect(Collectors.toList());

    // Save new discounts
    newVolunteerSlots.stream()
      .filter(volunteerSlot -> !existingVolunteerSlots.contains(volunteerSlot))
      .forEach(volunteerSlot -> slotService.save(new EventTypeSlot(slotService.findByTypeAndSlot("volunteer", volunteerSlot), event, volunteerSlot.getSeqNr())));

    // Remove existing discounts not in new discounts
    existingVolunteerSlots.stream()
      .filter(existingHostingSlot -> !newVolunteerSlots.contains(existingHostingSlot))
      .forEach(existingHostingSlot -> {
        EventTypeSlot eventTypeSlot = eventTypeSlotService.findByEventAndTypeSlot(event, slotService.findByTypeAndSlot("volunteer", existingHostingSlot));
        eventTypeSlotService.delete(eventTypeSlot);
      });
  }

  public void deleteVolunteering(JsonObject request, Event event) {
    event.setVolunteering(false);
    List<VolunteerType> volunteerTypes = event.getVolunteerTypes();
    volunteerTypes.clear();
    event.setVolunteerTypes(volunteerTypes);
    eventRepo.save(event);
    eventTypeSlotService.deleteByEventAndType(event, "volunteer");
  }

  public List<Map<String, String>> getVolunteerSchema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");               put("type", "id");           put("label", "id");}});
        add(new HashMap<>() {{put("key", "volunteering");     put("type", "bool");         put("labelTrue", "Enable Volunteering"); put("labelFalse", "Disable Volunteering");}});
        add(new HashMap<>() {{put("key", "volunteerSlots");   put("type", "multiselect");  put("required", "false");                put("label", "Special Volunteer Slots");   put("list", getVolunteerSlots().toString());}});
        add(new HashMap<>() {{put("key", "volunteerTypes");   put("type", "multiselect");  put("required", "true");                 put("label", "Volunteer Types");  put("list", getVolunteerTypes().toString());}});
        add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "volunteer");                          put("label", OutTextConfig.LABEL_VOLUNTEER_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "count");            put("type", "single");}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_VOLUNTEERING_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_VOLUNTEERING_EN.getOutTextKey()); }});

      }
    };
  }

  public List<Map<String, String>> getVolunteerSlots() {
    return
      slotService.getAllSlots( "volunteer")
      .stream()
      .map(slot -> slot.dataMap())
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getVolunteerTypes() {
    return volunteerTypeRepo.findAll()
      .stream()
      .map(volunteerType -> volunteerType.dataMap())
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getVolunteerData(List<Event> eventList) {
    List<Map<String, String>> volunteerData = new ArrayList<>();
    eventList.forEach(event -> {

      // discount data
      Map<String, String> volunteerMap = dataMap(event);
      volunteerData.add(volunteerMap);
    });
    return volunteerData;
  }


  public List<EventTypeSlot> eventVolunteerList (Event event) {
    return eventTypeSlotService.findByEventAndType(event, "volunteer");
  }


  public Map<String, String> dataMap(Event event) {
    return new HashMap<>() {
      {
        put("id", String.valueOf(event.getEventId()));
        put("volunteering", String.valueOf(event.isVolunteering()));
        put("volunteerSlots", slotService.getAllSlots("volunteer", event).stream().map(slot -> slot.getSlotId()).collect(Collectors.toList()).toString());
        put("volunteerTypes", volunteerTypeRepo.findAllByEvent(event).stream().map(volunteerType -> volunteerType.getVolunteerTypeId()).collect(Collectors.toList()).toString());
      }
    };
  }

}
