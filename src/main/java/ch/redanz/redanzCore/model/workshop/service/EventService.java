package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.exception.HasRegistrationException;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
  private final EventRepo eventRepo;
  private final EventBundleRepo eventBundleRepo;
  private final BundleService bundleService;
  private final OutTextService outTextService;
  private final DiscountService discountService;
  private final FoodService foodService;
  private final SpecialService specialService;
  private final PrivateClassService privateClassService;
  private final EventTypeSlotService eventTypeSlotService;
  private final SlotService slotService;
  private final EventPrivateClassRepo eventPrivateClassRepo;
  private final EventSpecialRepo eventSpecialRepo;
  private final EventDiscountRepo eventDiscountRepo;
  private final EventFoodSlotRepo eventFoodSlotRepo;
  private final EventTrackRepo eventTrackRepo;
  private final TrackService trackService;
  private final EventPartInfoRepo eventPartInfoRepo;
  private final EventPartService eventPartService;
  private final DanceRoleService danceRoleService;
  private final BundleEventTrackService bundleEventTrackService;
  private final EventDanceRoleRepo eventDanceRoleRepo;
  private final FoodRegistrationService foodRegistrationService;
  private final RegistrationRepo registrationRepo;
  private final DiscountRegistrationService discountRegistrationService;
  private final SpecialRegistrationService specialRegistrationService;

  public EventBundle findByEventAndBundle(Event event, Bundle bundle) {
    return eventBundleRepo.findByEventAndBundle(event, bundle);
  }

  public EventDanceRole findByEventAndDanceRole(Event event, DanceRole danceRole) {
    return eventDanceRoleRepo.findByEventAndDanceRole(event, danceRole);
  }

  public EventDiscount findByEventAndDiscount(Event event, Discount discount) {
    return eventDiscountRepo.findByEventAndDiscount(event, discount);
  }

  public EventPrivateClass findByEventAndPrivate(Event event, PrivateClass privateClass) {
    return eventPrivateClassRepo.findByEventAndPrivateClass(event, privateClass);
  }

  public EventSpecial findByEventAndSpecial(Event event, Special special) {
    return eventSpecialRepo.findByEventAndSpecial(event, special);
  }

  public EventTrack findByEventAndTrack(Event event, Track track) {
    return eventTrackRepo.findByEventAndTrack(event, track);
  }

  public BundleEventTrack findByBundleEventAndTrack(Bundle bundle, Event event, Track track) {
//    log.info("findByEventAndTrack: " + findByEventAndTrack(event, track));
//    log.info("bundle: " + bundle);
//    log.info("track: " + track);
    return bundleEventTrackService.findByBundleAndEventTrack(bundle, findByEventAndTrack(event, track));
  }

  public EventFoodSlot findEventFoodSlotByEventAndFood(Event event, Food food, Slot slot) {
    return eventFoodSlotRepo.findByEventAndFoodAndSlot(event, food, slot);
  }


  public void save(Event event) {
    eventRepo.save(event);
  }

  public void save(EventDanceRole eventDanceRole) {
    eventDanceRoleRepo.save(eventDanceRole);
  }

  public boolean eventBundleExists(Event event, Bundle bundle) {
    return eventBundleRepo.existsByEventAndBundle(event, bundle);
  }

  public boolean eventTrackExists(Event event, Track track) {
    return eventTrackRepo.existsByEventAndTrack(event, track);
  }

  public void save(EventBundle eventBundle) {
    eventBundleRepo.save(eventBundle);
  }

  public void save(EventTrack eventTrack) {
    eventTrackRepo.save(eventTrack);
  }

  public void save(EventDiscount eventDiscount) {
    eventDiscountRepo.save(eventDiscount);
  }

  public void save(EventPrivateClass eventPrivateClass) {
    eventPrivateClassRepo.save(eventPrivateClass);
  }

  public void save(EventPartInfo eventPartInfo) {
    eventPartInfoRepo.save(eventPartInfo);
  }

  public void save(EventSpecial eventSpecial) {
    eventSpecialRepo.save(eventSpecial);
  }

  public void save(EventFoodSlot eventFoodSlot) {
    eventFoodSlotRepo.save(eventFoodSlot);
  }

  public List<Event> getAllEvents() {
    return eventRepo.findAllByArchivedOrderByEventIdDesc(false);
  }

//  public Event getCurrentEvent() {
//    return eventRepo.findDistinctFirstByActive(true);
//  }

  public Event getById(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }

  public List<Event> getActiveEvents() {
    return eventRepo.findAllByActiveAndArchived(true, false);
  }

  public List<Event> getActiveEventsFuture() {
    List<Event> activeFutureEvents = new ArrayList<>();
    eventRepo.findAllByActiveAndArchived(true, false).forEach(
      event -> {
        if (event.getEventTo().isAfter(LocalDate.now())) {
          activeFutureEvents.add(event);
        }
      });
    return activeFutureEvents;
  }

  public List<Event> getInactiveEvents() {
    return eventRepo.findAllByActiveAndArchived(false, false);
  }

  public Event findByEventId(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }

  public Event findByName(String name) {
    return eventRepo.findByName(name);
  }

  public List<Event> findAll() {
    return eventRepo.findAll();
  }

  public boolean existsByName(String name) {
    return eventRepo.existsByName(name);
  }

  public List<Map<String, String>> getFoodSlotsMap() {
    return eventFoodSlotRepo.findAll().stream()
      .map(EventFoodSlot::dataMap)
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getEventSchema() {
    List<Map<String, String>> eventSchema = Event.schema();
    eventSchema.forEach(item -> {
      switch (item.get("key")) {
        case "discounts":
          item.put("list", discountService.getDiscountsMap().toString());
          break;
        case "foodSlots":
          item.put("list", getFoodSlotsMap().toString());
          break;
        case "specials":
          item.put("list", specialService.getSpecialsMap().toString());
          break;
        case "privates":
          item.put("list", privateClassService.getPrivatesMap().toString());
          break;
      }
    });
    return eventSchema;
  }

  public List<Map<String, String>> getSchemaStaticData() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "volunteerType");
          put("type", "attribute");
          put("label", "Volunteer Types");
        }});
        add(new HashMap<>() {{
          put("key", "singular");
          put("type", "title");
          put("label", OutTextConfig.LABEL_STATIC_DATA_EN.getOutTextKey());
        }});
        add(new HashMap<>() {{
          put("key", "plural");
          put("type", "title");
          put("label", OutTextConfig.LABEL_STATIC_DATA_EN.getOutTextKey());
        }});
        add(new HashMap<>() {{
          put("key", "special");
          put("type", "attribute");
          put("label", "Specials");
        }});
        add(new HashMap<>() {{
          put("key", "private");
          put("type", "attribute");
          put("label", "Private Classes");
        }});
        add(new HashMap<>() {{
          put("key", "slot");
          put("type", "attribute");
          put("label", "Slots");
        }});
        add(new HashMap<>() {{
          put("key", "food");
          put("type", "attribute");
          put("label", "Food");
        }});
        add(new HashMap<>() {{
          put("key", "discount");
          put("type", "attribute");
          put("label", "Discount");
        }});
        add(new HashMap<>() {{
          put("key", "name");
          put("type", "title");
          put("label", OutTextConfig.LABEL_STATIC_DATA_EN.getOutTextKey());
        }});
        add(new HashMap<>() {{
          put("key", "count");
          put("type", "single");
        }});
        add(new HashMap<>() {{
          put("key", "noAction");
          put("type", "noAction");
        }});
      }
    };
  }

  public List<Long> eventDiscountIdList(Event event) {
    return event.getEventDiscounts().stream()
      .map(eventDiscount -> eventDiscount.getEventDiscountId())
      .collect(Collectors.toList());
  }

  public List<Long> eventSpecialIdList(Event event) {
    return event.getEventSpecials().stream()
      .map(eventSpecial -> eventSpecial.getSpecial().getSpecialId())
      .collect(Collectors.toList());
  }

  public List<Long> eventPrivateIdList(Event event) {
    return event.getEventPrivates().stream()
      .map(eventPrivateClass -> eventPrivateClass.getEventPrivateClassId())
      .collect(Collectors.toList());
  }

  public List<Long> eventTypeSlotIdList(Event event, String type) {
    return event.getEventTypeSlots().stream()
      .filter(eventTypeSlot -> eventTypeSlot.getTypeSlot().getType().equals(type))
      .map(eventTypeSlot -> eventTypeSlot.getTypeSlot().getTypeSlotId())
      .collect(Collectors.toList());
  }

  public List<Long> eventFoodSlotIdList(Event event) {
    return event.getEventFoodSlots().stream()
      .map(eventFoodSlot -> eventFoodSlot.getEventFoodSlotId())
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getEventsData(List<Event> eventList) {
    List<Map<String, String>> eventsData = new ArrayList<>();
    eventList.forEach(event -> {
      Map<String, String> eventData = event.dataMap();
      eventData.put("discounts", eventDiscountIdList(event).toString());
      eventData.put("foodSlots", eventFoodSlotIdList(event).toString());
      eventData.put("specials", eventSpecialIdList(event).toString());
      eventData.put("privates", eventPrivateIdList(event).toString());
      eventsData.add(eventData);
//      log.info(eventsData.toString());
    });
    return eventsData;
  }

  public List<Map<String, String>> getStaticData() {
    List<Map<String, String>> staticDataList = new ArrayList<>();
    Map<String, String> staticData = new HashMap<>();
    staticDataList.add(staticData);
    return staticDataList;

  }

  public List<Map<String, String>> getBundleSchema(Event event) {
    List<Map<String, String>> bundleSchema = Bundle.schema();
    bundleSchema.forEach(item -> {
      switch (item.get("key")) {
        case "partySlots":
          item.put("list", slotService.typeSlotDataMap("party").toString());
          break;
        case "bundleSpecial":
          item.put("list", eventSpecialData(event).toString());
          break;
        case "tracks":
          item.put("list", eventTrackData(event).toString());
          break;
        case "bundleDanceRole":
          item.put("list", danceRoleService.getDanceRolesMap().toString());
          break;
      }
    });

    return bundleSchema;
  }

  public List<Map<String, String>> getBundlesData(Event event) {
    List<Map<String, String>> bundlesData = new ArrayList<>();
    Set<EventBundle> eventBundlesSet = event.getEventBundles();
    List<EventBundle> eventBundlesList = new ArrayList<>(eventBundlesSet);
    eventBundlesList.sort(Comparator.comparingInt(eventBundle -> eventBundle.getBundle().getSeqNr()));
    eventBundlesList.forEach(
      eventBundle -> {
        // bundle data
        Map<String, String> bundleData = eventBundle.getBundle().dataMap();

        // add event info
        bundleData.put("eventId", Long.toString(event.getEventId()));
        bundleData.put("partySlots", bundleService.partySlotIds(eventBundle.getBundle()).toString());
        bundleData.put("bundleSpecial", bundleService.bundleEventSpecialIds(eventBundle.getBundle()).toString());
        bundleData.put("bundleDanceRole", bundleService.bundleDanceroleIds(eventBundle.getBundle()).toString());
        bundleData.put("track", bundleService.bundleEventTrackIds(eventBundle.getBundle()).toString());
        bundleData.put("capacity", eventBundle.getCapacity().toString());
        bundlesData.add(bundleData);
      });

    return bundlesData;
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

  public Field getEventPrivateField(String key) {
    Field field;
    try {
      field = EventPrivateClass.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public Field getEventSpecialField(String key) {
    Field field;
    try {
      field = EventSpecial.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public Field getEventDiscountField(String key) {
    Field field;
    try {
      field = EventDiscount.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public Field getEventPartInfoField(String key) {
    Field field;
    try {
      field = EventPartInfo.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public Field getEventFoodSlotField(String key) {
    Field field;
    try {
      field = EventFoodSlot.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public List<Track> getAllTracksByEvent(Event event) {
    return event.getEventTracks().stream()
      .map(EventTrack::getTrack)
      .collect(Collectors.toList());
  }

  public void updateEventPartInfo(JsonObject request) {
    EventPartInfo eventPartInfo = eventPartInfoRepo.findByEventPartInfoId(request.get("id").getAsLong());

    EventPartInfo.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;
        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventPartInfoField(key);
                  field.set(eventPartInfo, outTextKey);
                }
              }

              break;
            case "text":
              field = getEventPartInfoField(key);
              field.set(eventPartInfo, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventPartInfoField(key);
              field.set(eventPartInfo, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventPartInfoField(key);
              field.set(eventPartInfo, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;


            case "bool":
              field = getEventPartInfoField(key);
              field.set(eventPartInfo, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(eventPartInfo);
  }

  public void updateEvent(JsonObject request) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Event event;
    boolean newEvent = false;
    List<Discount> newDiscounts = new ArrayList<>();
    List<Special> newSpecials = new ArrayList<>();
    Long eventId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (eventId == null || eventId == 0) {
      event = new Event();
      newEvent = true;
    } else {
      event = findByEventId(eventId);
    }

    Event.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;
//        log.info(type);
        try {
          switch (type) {
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
//              log.info(request.toString());
              String dateTimeDateString = request.get(key + "_date").getAsString();   // yyyy-MM-dd
              String dateTimeTimeString = request.get(key + "_time").isJsonNull()
                ? "12:00"
                : request.get(key + "_time").getAsString();
              // HH:mm

              ZoneId met = ZoneId.of("Europe/Berlin"); // (CET/CEST)
              ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDate.parse(dateTimeDateString), LocalTime.parse(dateTimeTimeString), met);
              Field zonedDateTimefield = getField(key);
              zonedDateTimefield.setAccessible(true);
              zonedDateTimefield.set(event, zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "multiselect":
            case "multiselectInfo":
              if (request.get(key) != null && request.get(key).isJsonArray()) {
                request.get(key).getAsJsonArray().forEach(item -> {
//                  log.info(item.toString());

                  switch (key) {
                    case "specials":
                      newSpecials.add(specialService.findBySpecialId(item.getAsLong()));
                      break;
                    case "discounts":
                      newDiscounts.add(discountService.findByDiscountId(item.getAsLong()));
                      break;
                  }
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
    save(event);
    if (newEvent) {
      eventPartService.newBaseEventPartInfo(event);
    }
    ;
  }


  public void updateEventPrivate(JsonObject request, Event event) throws IOException, TemplateException {
    EventPrivateClass eventPrivateClass;
    Long eventPrivateId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (eventPrivateId == null || eventPrivateId == 0) {
      eventPrivateClass = new EventPrivateClass();
    } else {
      eventPrivateClass = eventPrivateClassRepo.findByEventPrivateClassId(eventPrivateId);
    }

    eventPrivateClass.setEvent(event);
    EventPrivateClass.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventPrivateField(key);
                  field.set(eventPrivateClass, outTextKey);
                }
              }
              break;
            case "text":
              field = getEventPrivateField(key);

              field.set(eventPrivateClass, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventPrivateField(key);
              field.set(eventPrivateClass, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventPrivateField(key);
              field.set(eventPrivateClass, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;


            case "list":
              field = getEventPrivateField(key);
              field.set(eventPrivateClass, privateClassService.findByPrivateClassId(
                request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
              ));
              break;


            case "bool":
              field = getEventPrivateField(key);
              field.set(eventPrivateClass, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(eventPrivateClass);
  }

  public void updateEventSpecial(JsonObject request, Event event) throws IOException, TemplateException {
    EventSpecial eventSpecial;
    Long eventSpecialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    if (eventSpecialId == null || eventSpecialId == 0) {
      eventSpecial = new EventSpecial();
      eventSpecial.setSoldOut(false);
    } else {
      eventSpecial = eventSpecialRepo.findByEventSpecialId(eventSpecialId);
    }

    eventSpecial.setEvent(event);
    EventSpecial.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventSpecialField(key);
                  field.set(eventSpecial, outTextKey);
                }
              }
              break;
            case "text":
              field = getEventSpecialField(key);

              field.set(eventSpecial, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventSpecialField(key);
              field.set(eventSpecial, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventSpecialField(key);
              field.set(eventSpecial, request.get(key).isJsonNull() ? 0 : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "list":

              field = getEventSpecialField(key);
              field.set(eventSpecial, specialService.findBySpecialId(
                request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
              ));
              break;

            case "bool":
              field = getEventSpecialField(key);
              field.set(eventSpecial, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(eventSpecial);
  }

  public void updateEventDiscount(JsonObject request, Event event) throws IOException, TemplateException {
    EventDiscount eventDiscount;
    Long eventDiscountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (eventDiscountId == null || eventDiscountId == 0) {
      eventDiscount = new EventDiscount();
    } else {
      eventDiscount = eventDiscountRepo.findByEventDiscountId(eventDiscountId);
    }

    eventDiscount.setEvent(event);
    EventDiscount.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventDiscountField(key);
                  field.set(eventDiscount, outTextKey);
                }
              }
              break;
            case "text":
              field = getEventDiscountField(key);

              field.set(eventDiscount, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventDiscountField(key);
              field.set(eventDiscount, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventDiscountField(key);
              field.set(eventDiscount, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "list":
              field = getEventDiscountField(key);
              field.set(eventDiscount, discountService.findByDiscountId(
                request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
              ));
              break;

            case "bool":
              field = getEventDiscountField(key);
              field.set(eventDiscount, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(eventDiscount);
  }

  public void updateEventFoodSlot(JsonObject request, Event event) throws IOException, TemplateException {
    EventFoodSlot eventFoodSlot;
    Long eventFoodSlotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (eventFoodSlotId == null || eventFoodSlotId == 0) {
      eventFoodSlot = new EventFoodSlot();
    } else {
      eventFoodSlot = eventFoodSlotRepo.findByEventFoodSlotId(eventFoodSlotId);
    }

    eventFoodSlot.setEvent(event);
    EventFoodSlot.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventFoodSlotField(key);
                  field.set(eventFoodSlot, outTextKey);
                }
              }
              break;
            case "text":
              field = getEventFoodSlotField(key);

              field.set(eventFoodSlot, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventFoodSlotField(key);
              field.set(eventFoodSlot, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventFoodSlotField(key);
              field.set(eventFoodSlot, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "list":
              field = getEventFoodSlotField(key);
              Long listId = request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString());

              switch (key) {
                case "food":
                  field.set(eventFoodSlot, foodService.findByFoodId(listId));
                  break;
                case "slot":
                  field.set(eventFoodSlot, slotService.findBySlotId(listId));
                  break;
              }
              break;

            case "bool":
              field = getEventFoodSlotField(key);
              field.set(eventFoodSlot, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(eventFoodSlot);
  }

  @Transactional
  public void archiveEvent(Event event) {
    event.setArchived(true);
    event.setActive(false);
    save(event);
  }

  @Transactional
  public void delete(Event event, Track track) {
    List<EventTrack> eventTracks = new ArrayList<>(event.getEventTracks());
    eventTracks.forEach(
      eventTrack -> {
        if (eventTrack.getTrack().equals(track)) {
          bundleEventTrackService.deleteAllByEventTrack(eventTrack);
          event.getEventTracks().remove(eventTrack);
          trackService.delete(eventTrack.getTrack());
          delete(eventTrack);
        }
      });
    save(event);
  }

  @Transactional
  public void deleteEvent(Event event) {
    Set<EventBundle> eventBundles = new HashSet<>(event.getEventBundles());
    eventBundles.forEach(
      eventBundle -> {
        event.getEventBundles().remove(eventBundle);
        bundleService.deleteBundle(eventBundle.getBundle());
        delete(eventBundle);
      });
    save(event);

    List<EventTrack> eventTracks = new ArrayList<>(event.getEventTracks());
    eventTracks.forEach(
      eventTrack -> {
        event.getEventTracks().remove(eventTrack);
        trackService.delete(eventTrack.getTrack());
        delete(eventTrack);
      });
    save(event);

    List<EventFoodSlot> eventFoodSlots = new ArrayList<>(event.getEventFoodSlots());
    eventFoodSlots.forEach(
      eventFoodSlot -> {
        event.getEventFoodSlots().remove(eventFoodSlot);
        delete(eventFoodSlot);
      });
    save(event);

    List<EventPrivateClass> eventPrivateClasses = new ArrayList<>(event.getEventPrivates());
    eventPrivateClasses.forEach(
      eventPrivateClass -> {
        event.getEventPrivates().remove(eventPrivateClass);
        delete(eventPrivateClass);
      });
    save(event);

    event.setVolunteerTypes(new ArrayList<>());
    save(event);

    List<EventSpecial> eventSpecials = new ArrayList<>(event.getEventSpecials());
    eventSpecials.forEach(
      eventSpecial -> {
        event.getEventSpecials().remove(eventSpecial);
        delete(eventSpecial);
      });
    save(event);

    List<EventDiscount> eventDiscounts = new ArrayList<>(event.getEventDiscounts());
    eventDiscounts.forEach(
      eventDiscount -> {
        event.getEventDiscounts().remove(eventDiscount);
        delete(eventDiscount);
      });
    save(event);

    List<EventTypeSlot> eventTypeSlots = new ArrayList<>(event.getEventTypeSlots());
    eventTypeSlots.forEach(
      eventTypeSlot -> {
        event.getEventTypeSlots().remove(eventTypeSlot);
        delete(eventTypeSlot);
      });
    save(event);

    eventRepo.delete(event);
  }

  public void deleteEventFoodSlot(JsonObject request) {
    try {
      Long eventFoodSlotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Long eventId = request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong();
      Long foodId = request.get("food").isJsonNull() ? null : request.get("food").getAsLong();
      if (foodRegistrationService.hasRegistrations(findByEventId(eventId), foodService.findByFoodId(foodId), true)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        delete(eventFoodSlotRepo.findByEventFoodSlotId(eventFoodSlotId));
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  public void deleteEventPrivate(JsonObject request) {
    try {
      Long eventPrivateId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Long privateClassId = request.get("privateClass").isJsonNull() ? null : request.get("privateClass").getAsLong();
      Long eventId = request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong();
      if (privateClassService.hasRegistration(findByEventId(eventId), privateClassService.findByPrivateClassId(privateClassId), true)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        delete(eventPrivateClassRepo.findByEventPrivateClassId(eventPrivateId));
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }


  public void deleteEventDanceRole(JsonObject request) {
    try {
      Long eventDanceRoleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Long danceRoleId = request.get("danceRole").isJsonNull() ? null : request.get("danceRole").getAsLong();
      Long eventId = request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong();
      if (registrationRepo.existsByActiveAndEventAndDanceRole(true, findByEventId(eventId), danceRoleService.findByDanceRoleId(danceRoleId))) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        eventDanceRoleRepo.deleteById(eventDanceRoleId);
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }
  public void deleteEventSpecial(JsonObject request) {
    try {
      Long eventSpecialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Long eventId = request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong();
      Long specialId = request.get("special").isJsonNull() ? null : request.get("special").getAsLong();
      if (specialRegistrationService.hasRegistrations(findByEventId(eventId), specialService.findBySpecialId(specialId), true)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        delete(eventSpecialRepo.findByEventSpecialId(eventSpecialId));
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  public void deleteEventDiscount(JsonObject request) {
    try {
      Long eventDiscountId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
      Long eventId = request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong();
      Long discountId = request.get("discount").isJsonNull() ? null : request.get("discount").getAsLong();
      if (discountRegistrationService.hasRegistrations(findByEventId(eventId), discountService.findByDiscountId(discountId), true)) {
        throw new HasRegistrationException(OutTextConfig.LABEL_ERROR_HAS_EVENT_GE.getOutTextKey());
      } else {
        delete(eventDiscountRepo.findByEventDiscountId(eventDiscountId));
      }
    } catch (HasRegistrationException hasRegistrationException) {
      throw new ApiRequestException(hasRegistrationException.getMessage(), HttpStatus.CONFLICT);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  public void delete(EventBundle eventBundle) {
    eventBundle.getEvent().getEventBundles().remove(eventBundle);
    eventBundleRepo.delete(eventBundle);
  }

  public void delete(EventTrack eventTrack) {
    eventTrackRepo.delete(eventTrack);
  }

  @Transactional
  public void delete(EventTypeSlot eventTypeSlot) {
    eventTypeSlot.getEvent().getEventTypeSlots().remove(eventTypeSlot);
  }

  @Transactional
  public void delete(EventPrivateClass eventPrivate) {
    eventPrivate.getEvent().getEventPrivates().remove(eventPrivate);
  }

  @Transactional
  public void delete(EventDiscount eventDiscount) {
    eventDiscount.getEvent().getEventDiscounts().remove(eventDiscount);
  }


  @Transactional
  public void delete(EventSpecial eventSpecial) {
    eventSpecial.getEvent().getEventSpecials().remove(eventSpecial);
  }

  @Transactional
  public void delete(EventFoodSlot slot) {
    slot.getEvent().getEventFoodSlots().remove(slot);
  }

  public void clone(Event baseEvent) {
    List<VolunteerType> newVolunteertypes = new ArrayList<>(baseEvent.getVolunteerTypes());
    Event newEvent = new Event(
      baseEvent.getName() + " [clone]",
      baseEvent.getCapacity(),
      baseEvent.getEventFrom(),
      baseEvent.getEventTo(),
      baseEvent.getRegistrationStart(),
      false, // inactive by default
      false, // not archived by default
      baseEvent.getDescription(),
      baseEvent.isHosting(),
      baseEvent.isVolunteering(),
      baseEvent.isScholarship(),
      newVolunteertypes,
      true
    );
    save(newEvent);

    // Copy Type Slots
    baseEvent.getEventTypeSlots().forEach(
      baseEventTypeSlot -> {
        slotService.save(new EventTypeSlot(
          baseEventTypeSlot.getTypeSlot(),
          newEvent,
          baseEventTypeSlot.getSeqNr()
        ));
      });

    // Clone Private Classes
    baseEvent.getEventPrivates().forEach(eventPrivateClass -> {
      EventPrivateClass newPrivateClass = new EventPrivateClass(
        eventPrivateClass.getPrivateClass(),
        newEvent,
        eventPrivateClass.getPrice(),
        false, // initialize not sold out
        eventPrivateClass.getCapacity()
      );
      save(newPrivateClass);
    });

    // Clone Specials
    Set<EventSpecial> eventSpecials = new HashSet<>();
    baseEvent.getEventSpecials().forEach(eventSpecial -> {
      eventSpecials.add(new EventSpecial(
          eventSpecial.getSpecial(),
          newEvent,
          eventSpecial.getPrice(),
          false, // initialize not sold out
          eventSpecial.getCapacity(),
          eventSpecial.getUrl(),
          eventSpecial.getInfoOnly()
        )
      );
    });
    newEvent.setEventSpecials(eventSpecials);

    // Clone Discounts
    Set<EventDiscount> eventDiscounts = new HashSet<>();
    baseEvent.getEventDiscounts().forEach(eventDiscount -> {
      eventDiscounts.add(
        new EventDiscount(
          eventDiscount.getDiscount(),
          newEvent,
          eventDiscount.getDiscountAmount(),
          eventDiscount.getCapacity()
        )
      );
    });
    newEvent.setEventDiscounts(eventDiscounts);

    // Clone Food
    Set<EventFoodSlot> eventFoodSlots = new HashSet<>();
    baseEvent.getEventFoodSlots().forEach(eventFoodSlot -> {
      eventFoodSlots.add(new EventFoodSlot(
          eventFoodSlot.getFood(),
          eventFoodSlot.getSlot(),
          newEvent,
          eventFoodSlot.getPrice(),
          eventFoodSlot.getSeqNr()
        )
      );
    });
    newEvent.setEventFoodSlots(eventFoodSlots);
    save(newEvent);

    // Clone Event Dance Roles
    Set<EventDanceRole> newEventDanceroles = new HashSet<>();
    baseEvent.getEventDanceRoles().forEach(baseEventDanceRole -> {
      newEventDanceroles.add(new EventDanceRole(
        newEvent
        ,baseEventDanceRole.getDanceRole()
        ,baseEventDanceRole.getHint()
      ));
    });
    newEvent.setEventDanceRoles(newEventDanceroles);
    save(newEvent);


    // Clone Tracks
    Map<BundleEventTrack, EventTrack> baseBundleEventTrackNewEventTrackList = new HashMap<>();
//    Set<Bundle> baseBundles = baseEvent.getEventBundles().stream().map(EventBundle::getBundle)
//      .collect(Collectors.toSet());
    baseEvent.getEventBundles().forEach(baseEventBundle -> {
      if (bundleService.hasTrack(baseEventBundle.getBundle()))
        baseEventBundle.getBundle().getBundleEventTracks().forEach(
          baseBundleEventTrack -> {
            baseBundleEventTrackNewEventTrackList.put(baseBundleEventTrack, null);
      });
    });

    newEvent.setEventTracks(new HashSet<>());
    baseEvent.getEventTracks().forEach(baseEventTrack -> {
      Track baseTrack = baseEventTrack.getTrack();
      EventTrack newEventTrack = trackService.clone(newEvent, baseTrack);
      save(newEvent);

      baseEvent.getEventBundles().forEach(baseEventBundle -> {
        baseEventBundle.getBundle().getBundleEventTracks().forEach(
          baseBundleEventTrack -> {
          if (baseBundleEventTrack.getEventTrack().equals(baseEventTrack)) {
            baseBundleEventTrackNewEventTrackList.put(baseBundleEventTrack, newEventTrack);
          }
        });

      });
    });
    save(newEvent);

    // Clone Bundles
    Set<EventBundle> eventBundles = new HashSet<>();
    baseEvent.getEventBundles().forEach(
      baseEventBundle -> {
        Bundle baseBundle = baseEventBundle.getBundle();
        Bundle newBundle = bundleService.clone(newEvent, baseBundle, baseBundleEventTrackNewEventTrackList);
        eventBundles.add(new EventBundle(newBundle, newEvent, baseEventBundle.getCapacity()));
      });
    newEvent.setEventBundles(eventBundles);
    save(newEvent);

    // clone eventPartInfo
    eventPartService.clone(baseEvent, newEvent);
  }

  public List<EventPrivateClass> findPrivateClassesByEvent(Event event) {
    List<EventPrivateClass> eventPrivateClasses = new ArrayList<>(event.getEventPrivates());
    return eventPrivateClasses;

  }

  public List<EventPrivateClass> findPrivateClassesByEventAndBundle(Event event, Bundle bundle) {
    if (bundle.getSimpleTicket()) {
      return new ArrayList<>();
    } else {
      return findPrivateClassesByEvent(event);
    }
  }

  public List<EventSpecial> findSpecialsByEvent(Event event) {
    List<EventSpecial> eventSpecials = new ArrayList<>(event.getEventSpecials());
    return eventSpecials;
  }

  public List<EventSpecial> findSpecialsByEventAndBundle(Event event, Bundle bundle) {
    if (bundle.getSimpleTicket()) {
      return new ArrayList<>();
    } else {
      return findSpecialsByEvent(event);
    }
  }

  public List<Map<String, String>> eventPrivateSchema() {
    List<Map<String, String>> eventPrivateSchema = EventPrivateClass.schema();
    eventPrivateSchema.forEach(item -> {
      switch (item.get("key")) {
        case "privateClass":
          item.put("list", privateClassService.getAllPrivates().toString());
          break;
      }
    });
    return eventPrivateSchema;
  }

  public List<Map<String, String>> eventSpecialSchema() {
    List<Map<String, String>> eventSpecialSchema = EventSpecial.schema();
    eventSpecialSchema.forEach(item -> {
      switch (item.get("key")) {
        case "special":
          item.put("list", specialService.getAllSpecials().toString());
          break;
      }
    });
    return eventSpecialSchema;
  }

  public List<Map<String, String>> eventDiscountSchema() {
    List<Map<String, String>> eventDiscountSchema = EventDiscount.schema();
    eventDiscountSchema.forEach(item -> {
      switch (item.get("key")) {
        case "discount":
          item.put("list", discountService.getAllDiscounts().toString());
          break;
      }
    });
    return eventDiscountSchema;
  }

  public List<Map<String, String>> eventDanceRoleSchema() {
    List<Map<String, String>> eventDanceRoleSchema = EventDanceRole.schema();
    eventDanceRoleSchema.forEach(item -> {
      switch (item.get("key")) {
        case "danceRole":
          item.put("list", danceRoleService.getDanceRolesMap().toString());
          break;
      }
    });
    return eventDanceRoleSchema;
  }

  public List<Long> eventPrivatesIds(Event event) {
    return event.getEventPrivates().stream()
      .map(EventPrivateClass::getEventPrivateClassId)
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> eventPrivateData(Event event) {
    List<Map<String, String>> eventPrivateDataList = new ArrayList<>();
    event.getEventPrivates().forEach(
      eventPrivate -> {
        Map<String, String> eventPrivateData = eventPrivate.dataMap();
        eventPrivateData.put("eventId", Long.toString(event.getEventId()));
        eventPrivateData.put("id", Long.toString(eventPrivate.getEventPrivateClassId()));
        eventPrivateData.put("privateClass", Long.toString(eventPrivate.getPrivateClass().getPrivateClassId()));
        eventPrivateData.put("capacity", String.valueOf(eventPrivate.getCapacity()));
        eventPrivateData.put("price", String.valueOf(eventPrivate.getPrice()));
        eventPrivateDataList.add(eventPrivateData);
      });
    return eventPrivateDataList;
  }

  public List<Map<String, String>> eventSpecialData(Event event) {
    List<Map<String, String>> eventSpecialDataList = new ArrayList<>();
    event.getEventSpecials().forEach(
      eventSpecial -> {
        Map<String, String> eventSpecialData = eventSpecial.dataMap();
        eventSpecialData.put("eventId", Long.toString(event.getEventId()));
        eventSpecialData.put("id", Long.toString(eventSpecial.getEventSpecialId()));
        eventSpecialData.put("special", Long.toString(eventSpecial.getSpecial().getSpecialId()));
        eventSpecialData.put("name", eventSpecial.getSpecial().getName());
        eventSpecialData.put("capacity", String.valueOf(eventSpecial.getCapacity()));
        eventSpecialData.put("price", String.valueOf(eventSpecial.getPrice()));
        eventSpecialDataList.add(eventSpecialData);
      });
    return eventSpecialDataList;
  }

  public List<Map<String, String>> eventTrackData(Event event) {
    List<Map<String, String>> eventTrackDataList = new ArrayList<>();
    event.getEventTracks().forEach(
      eventTrack -> {
        Map<String, String> eventTrackData = eventTrack.dataMap();
        eventTrackData.put("eventId", Long.toString(event.getEventId()));
        eventTrackData.put("id", Long.toString(eventTrack.getEventTrackId()));
        eventTrackData.put("track", Long.toString(eventTrack.getTrack().getTrackId()));
        eventTrackData.put("name", eventTrack.getTrack().getName());
        eventTrackDataList.add(eventTrackData);
      });
    return eventTrackDataList;
  }

  public List<Map<String, String>> eventDiscountData(Event event) {
    List<Map<String, String>> eventDiscountDataList = new ArrayList<>();
    event.getEventDiscounts().forEach(
      eventDiscount -> {
        Map<String, String> eventDiscountData = eventDiscount.dataMap();
        eventDiscountData.put("eventId", Long.toString(event.getEventId()));
        eventDiscountData.put("id", Long.toString(eventDiscount.getEventDiscountId()));
        eventDiscountData.put("discount", Long.toString(eventDiscount.getDiscount().getDiscountId()));
        eventDiscountData.put("name", eventDiscount.getDiscount().getName());
        eventDiscountData.put("capacity", String.valueOf(eventDiscount.getCapacity()));
        eventDiscountData.put("discountAmount", String.valueOf(eventDiscount.getDiscountAmount()));
        eventDiscountDataList.add(eventDiscountData);
      });
    return eventDiscountDataList;
  }

  public List<Map<String, String>> eventDanceRoleData(Event event) {
    List<Map<String, String>> eventDanceRoleDataList = new ArrayList<>();
    event.getEventDanceRoles().forEach(
      eventDanceRole -> {
        Map<String, String> evetDanceRoleData = eventDanceRole.dataMap();
        evetDanceRoleData.put("eventId", Long.toString(event.getEventId()));
        evetDanceRoleData.put("id", Long.toString(eventDanceRole.getEventDanceRoleId()));
        evetDanceRoleData.put("name", eventDanceRole.getDanceRole().getName());
        evetDanceRoleData.put("hint", eventDanceRole.getHint());
        eventDanceRoleDataList.add(evetDanceRoleData);
      });
    return eventDanceRoleDataList;
  }


  public boolean hasEventSpecial(Event event, Special special) {
    return eventSpecialRepo.existsByEventAndSpecial(event, special);
  }

  public List<Map<String, String>> getTermsSchema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "id");
          put("type", "id");
          put("label", "id");
        }});
        add(new HashMap<>() {{
          put("key", "requireTerms");
          put("type", "bool");
          put("labelTrue", "Enable Terms");
          put("labelFalse", "Disable Terms");
        }});
        add(new HashMap<>() {{
          put("key", "eventPartInfo");
          put("type", "partInfo");
          put("eventPartKey", "terms");
          put("label", OutTextConfig.LABEL_TERMS_INFO_EN.getOutTextKey());
        }});
        add(new HashMap<>() {{
          put("key", "plural");
          put("type", "title");
          put("label", OutTextConfig.LABEL_TERMS_EN.getOutTextKey());
        }});
        add(new HashMap<>() {{
          put("key", "singular");
          put("type", "title");
          put("label", OutTextConfig.LABEL_TERMS_EN.getOutTextKey());
        }});

      }

    };
  }

  public Map<String, String> termsDataMap(Event event) {
    return new HashMap<>() {
      {
        put("id", String.valueOf(event.getEventId()));
        put("requireTerms", String.valueOf(event.isRequireTerms()));
      }
    };
  }

  public List<Map<String, String>> getTermsData(Event event) {
    List<Map<String, String>> termsData = new ArrayList<>();
    termsData.add(termsDataMap(event));
    return termsData;
  }

  public void updateTerms(JsonObject request, Event event) throws IOException, TemplateException {
    getTermsSchema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "bool":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
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
  }

  public void deleteTerms(JsonObject request, Event event) {
    event.setRequireTerms(false);
    eventRepo.save(event);
  }

  @Transactional(readOnly = true)
  public Integer getEventCapacity(Event event) {
    return eventRepo.findCapacityByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public boolean getEventSoldOut(Event event) {
    return eventRepo.findSoldOutByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public Set<EventBundle> findAllEventBundles(Event event) {
    return eventRepo.findAllEventBundlesByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public Set<EventTrack> findAllEventTracks(Event event) {
    return eventRepo.findAllEventTracksByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public Set<EventSpecial> findAllEventSpecials(Event event) {
    return eventRepo.findAllEventSpecialsByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public Set<EventPrivateClass> findAllEventPrivates(Event event) {
    return eventRepo.findAllEventPrivatesByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public String findEventName(Event event) {
    return eventRepo.findEventNameByEventId(event.getEventId());
  }

  @Transactional(readOnly = true)
  public boolean findIsActive(Event event) {
    return eventRepo.findIsActiveByEventId(event.getEventId());
  }
//  public Integer eventCapacity(Event event) {
//    return eventRepo.findCapacityByEventId(event.getEventId());
//  }


  public Field getEventDanceRoleField(String key) {
    Field field;
    try {
      field = EventDanceRole.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateEventDanceRole(JsonObject request) throws IOException, TemplateException {
    Long eventDanceRoleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    EventDanceRole eventDanceRole;
    if (eventDanceRoleId == null || eventDanceRoleId == 0) {
      eventDanceRole = new EventDanceRole();
      eventDanceRole.setEvent(findByEventId(request.get("eventId").isJsonNull() ? null : request.get("eventId").getAsLong()));
    } else {
      eventDanceRole = eventDanceRoleRepo.findByEventDanceRoleId(eventDanceRoleId);
    }

    EventDanceRole.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch (type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());

                if (outTextKey != null) {
                  field = getEventDanceRoleField(key);
                  field.set(eventDanceRole, outTextKey);
                }
              }
              break;
            case "text":
              field = getEventDanceRoleField(key);
              field.set(eventDanceRole, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getEventDanceRoleField(key);
              field.set(eventDanceRole, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getEventDanceRoleField(key);
              field.set(eventDanceRole, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;


            case "color":
              field = getEventDanceRoleField(key);
              field.set(eventDanceRole, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "bool":
              field = getEventDanceRoleField(key);
              field.set(eventDanceRole, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            case "listText":
              field = getEventDanceRoleField(key);
              switch (key) {
                case "danceRole":
                  field.set(eventDanceRole, danceRoleService.findByDanceRoleId(
                    request.get(key).isJsonNull() ? null : Long.parseLong(request.get(key).getAsString())
                  ));
                  break;
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
    save(eventDanceRole);
  }


}
