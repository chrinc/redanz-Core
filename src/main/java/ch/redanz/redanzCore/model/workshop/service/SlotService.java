package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.workshop.config.SlotType;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  private final EventSlotRepo eventSlotRepo;
  private final OutTextService outTextService;

//  public void save(Slot slot) {
//    slotRepo.save(slot);
//  }

//  public boolean foodSlotExists(
//    Slot slot, Food food
//  ) {
//    return  typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot("food", food.getFoodId(), slot);
//  }

//  public TypeSlot findByTypeObjectIdAndSlot(
//    String type
//    ,Long typeObjectId
//    ,Slot slot
//  ) {
//    return typeSlotRepo.findByTypeAndTypeObjectIdAndSlot(
//      type,typeObjectId, slot
//    );
//  }

  public boolean eventSlotExistsByName(String name) {
    return eventSlotRepo.existsByName(name);
  }
  public EventSlot findEventSlotByName(String name) {
    return eventSlotRepo.findByName(name);
  }

//  public boolean typeSlotExists(String type, Long objectTypeId, Slot slot) {
//    return typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot(type, objectTypeId,  slot);
//  }

//  @Transactional
//  public void delete(TypeSlot typeSlot) {
//    typeSlotRepo.deleteById(typeSlot.getTypeSlotId());
//  }
//
//  public void delete(Slot slot) {
//    try {
//      slotRepo.delete(slot);
//    } catch (Exception exception){
//    }
//  }

//  public void save(TypeSlot typeSlot) {
//    typeSlotRepo.save(typeSlot);
//  }

//  public List<Slot> getAllVolunteerSlots(Event event) {
//    return getAllSlots("volunteer", event);
//  }
//
//  public boolean eventHasTypeSlot(Event event, TypeSlot typeSlot) {
//    return eventSlotRepo.findAllByEventAndTypeSlot(event, typeSlot).isPresent();
//  }

  public List<EventSlot> findAllByEvent (Event event){

    return eventSlotRepo.findAllByEvent(event);
  }
//  }

  public List<EventSlot> getAccommodationSlots(Event event) {
    return getAllEventSlots(SlotType.ACCOMMODATION, event);
  }
  public EventSlot findByEventSlotId(Long eventSlotId) {
    return eventSlotRepo.findByEventSlotId(eventSlotId);
  }
//  public Slot findByName(String name) {
//    return slotRepo.findByName(name);
//  }
//  public TypeSlot findByTypeAndSlotName(String type, String slotName) {
//    return typeSlotRepo.findByTypeAndSlot(
//      type,
//      slotRepo.findByName(slotName)
//    );
//  }

//  public TypeSlot findByTypeSlotId(Long typeSlotId) {
//    return typeSlotRepo.findByTypeSlotId(typeSlotId);
//  }
//
//  public TypeSlot findByTypeAndSlot(String type, Slot slot) {
//    return typeSlotRepo.findByTypeAndSlot(
//      type,
//      slot
//    );
//  }
//
  public List<EventSlot> getAllEventSlots(Event event) {
    return eventSlotRepo.findAllByEvent(event);
  }


  public List<Map<String, String>> slotTypeMap() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("name", SlotType.ACCOMMODATION.getLabel()); put("key", SlotType.ACCOMMODATION.name()); put("id", String.valueOf(SlotType.ACCOMMODATION.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.PARTY.getLabel()); put("key", SlotType.PARTY.name()); put("id", String.valueOf(SlotType.PARTY.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.VOLUNTEER.getLabel()); put("key", SlotType.VOLUNTEER.name()); put("id", String.valueOf(SlotType.VOLUNTEER.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.FOOD.getLabel()); put("key", SlotType.FOOD.name()); put("id", String.valueOf(SlotType.FOOD.getCode()));}});
      }
    };
  }

  public List<EventSlot> getAllEventSlots(SlotType type, Event event) {
    return eventSlotRepo.findAllByEventAndSlotType(event, type);
  }

  public List<Map<String, String>> eventSlotSchema(EventSlot eventSlot) {
    List<Map<String, String>> eventSlotSchema = EventSlot.schema();
    eventSlotSchema.forEach(item -> {
      if (item.get("key").equals("slotTypes")) {
        item.put("list", slotTypeMap().toString());
      }
    });
    return eventSlotSchema;
  }

  public List<Map<String, String>> eventSlotData(Event event, SlotType type) {
    List<Map<String, String>> eventSlotDataList = new ArrayList<>();
    getAllEventSlots(type, event).forEach(
      eventSlot -> {
        Map<String, String> eventSlotData = eventSlot.dataMap();
        eventSlotData.put("eventId", Long.toString(event.getEventId()));
        eventSlotDataList.add(eventSlotData);
      });
    return eventSlotDataList;
  }

  public List<Map<String, String>> eventSlotData(Event event) {
    List<Map<String, String>> eventSlotDataList = new ArrayList<>();
    getAllEventSlots(event).forEach(
      eventSlot -> {
        Map<String, String> eventSlotData = eventSlot.dataMap();
        eventSlotData.put("eventId", Long.toString(event.getEventId()));
//        eventSlotData.put("slotType", type.name());
        eventSlotDataList.add(eventSlotData);
      });
    return eventSlotDataList;
  }
//
//  public List<Slot> getAllSlots(String type) {
//    return typeSlotRepo.findAllByType(type).stream()
//      .filter(typeSlot -> typeSlot.getType().equals(type))
//      .map(TypeSlot::getSlot)
////      .sorted(Comparator.comparing(Slot::getSeqNr))
//      .collect(Collectors.toList());
//  }
//
//  public List<Map<String, String>> getAllSlots() {
//    List<Map<String, String>> slots = new ArrayList<>();
//
//    slotRepo.findAll().forEach(slot -> {
//      Map<String, String> slotData = slot.dataMap();
//      slots.add(slotData);
//    });
//
//    return slots;
//  }
//
//  public boolean existsEventSlotType(Event event, TypeSlot typeSlot) {
//    return eventSlotRepo.existsByEventAndTypeSlot(event, typeSlot);
//  }
//
//  public void updateTypeSlot(String type, Slot slot, Long typeObjectId) {
//    if (!typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot(type, typeObjectId, slot)) {
//      typeSlotRepo.save(
//        new TypeSlot(
//          type,
//          slot,
//          typeObjectId,
//
//        )
//      );
//    }
//  };
//
//  public List<Long> typeSlotIds(String type) {
//    return typeSlotRepo.findAllByType(type).stream()
//      .map(TypeSlot::getSlot)
//      .sorted(Comparator.comparing(Slot::getSeqNr))
//      .map(Slot::getSlotId)
//      .collect(Collectors.toList());
//  }
//
//  public List<Map<String, String>> typeSlotDataMap(String type) {
//    List<Map<String, String>> slotMap = new ArrayList<>();
//
//    typeSlotRepo.findAllByType(type).forEach(typeSlot -> {
//      Map<String, String> typeSlotDataMap = typeSlot.getSlot().dataMap();
//      slotMap.add(typeSlotDataMap);
//    });
//
//    return slotMap;
//  }
//
//  public List<Map<String, String>> getSlotSchema() {
//    return Slot.schema();
//  }
//  public List<Map<String, String>> getSlotData() {
//    List<Map<String, String>> slotsData = new ArrayList<>();
//    slotRepo.findAll().forEach(slot -> {
//      // discount data
//      Map<String, String> slotData = slot.dataMap();
//      slotsData.add(slotData);
//    });
//    return slotsData;
//  }
//
//  public void delete(JsonObject request) {
//    Long slotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//    delete(findBySlotId(slotId));
//  }
//
//  public boolean isUsed(Slot slot) {
//    return eventSlotRepo.existsByTypeSlotSlot(slot);
//  }
//
//  public Field getField(String key) {
//    Field field;
//    try {
//      field = Slot.class.getDeclaredField(key);
//    } catch (NoSuchFieldException e) {
//      throw new RuntimeException(e);
//    }
//    field.setAccessible(true);
//    return field;
//  }
//
//  public void update(JsonObject request) throws IOException, TemplateException {
//    Long id = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
//    Slot slot;
//
//    if (id == null || id == 0) {
//      slot = new Slot();
//    } else {
//      slot = findBySlotId(id);
//    }
//
//    Slot.schema().forEach(
//      stringStringMap -> {
//        String key = stringStringMap.get("key");
//        String type = stringStringMap.get("type");
//        Field field;
//
//        try {
//          switch(type) {
//            case "label":
//              if (request.get("label") != null && request.get("label").isJsonArray()) {
//                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
//                field =  getField(key);
//                field.set(slot, outTextKey);
//              }
//
//              break;
//            case "text":
//              field = getField(key);
//              field.set(slot, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
//              break;
//
//            case "number":
//              field = getField(key);
//              field.set(slot, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
//              break;
//
//            case "double":
//              field = getField(key);
//              field.set(slot, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
//              break;
//
//            case "color":
//              field = getField(key);
//              field.set(slot, request.get(key).isJsonNull() ? null :
//                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
//                  request.get(key).getAsJsonObject().get("hex").getAsString());
//              break;
//
//            case "bool":
//              field = getField(key);
//              field.set(slot, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
//              break;
//
//            default:
//              // Nothing will happen here
//          }
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    );
//    save(slot);
//  }

  public String slotNames(List<EventSlot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }
    // Sort the list by seqNr before processing
    slots.sort(Comparator.comparing(EventSlot::getSeqNr));

    return slots.stream()
      .map(slot -> outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText())
      .collect(Collectors.joining(", "));
  }

  public String slotNames(Set<EventSlot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }

    // Convert the Set to a List
    return slotNames(slots.stream().collect(Collectors.toList()), language);
  }

  public void save (EventSlot eventSlot) {
    this.eventSlotRepo.save(eventSlot);
  }
}
