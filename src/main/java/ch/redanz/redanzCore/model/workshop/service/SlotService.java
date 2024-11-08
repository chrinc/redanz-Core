package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.SlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.TypeSlotRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  private final TypeSlotRepo typeSlotRepo;
  private final SlotRepo slotRepo;
  private final EventTypeSlotRepo eventTypeSlotRepo;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;

  public void save(Slot slot) {
    slotRepo.save(slot);
  }

  public boolean foodSlotExists(
    Slot slot, Food food
  ) {
    return  typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot("food", food.getFoodId(), slot);
  }

//  public boolean foodSlotExists(
//    Slot slot, Food food
//  ) {
//    return  typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot("food", food.getFoodId());
//  }

  public TypeSlot findByTypeObjectIdAndSlot(
    String type
    ,Long typeObjectId
    ,Slot slot
  ) {
    return typeSlotRepo.findByTypeAndTypeObjectIdAndSlot(
      type,typeObjectId, slot
    );
  }

  public boolean existsByName(String name) {
    return slotRepo.existsByName(name);
  }

  public boolean typeSlotExists(String type, Long objectTypeId, Slot slot) {
    return typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot(type, objectTypeId,  slot);
  }

  @Transactional
  public void delete(TypeSlot typeSlot) {
    typeSlotRepo.deleteById(typeSlot.getTypeSlotId());
  }

  public void delete(Slot slot) {
    try {
      slotRepo.delete(slot);
    } catch (Exception exception){
    }
  }

  public void save(TypeSlot typeSlot) {
    typeSlotRepo.save(typeSlot);
  }

  public List<Slot> getAllVolunteerSlots(Event event) {
    return getAllSlots("volunteer", event);
  }

  public boolean eventHasTypeSlot(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.findAllByEventAndTypeSlot(event, typeSlot).isPresent();
  }

  public List<EventTypeSlot> findAllByEvent (Event event){
    return eventTypeSlotRepo.findAllByEvent(event);
  }
  public List<TypeSlot> findAllByType (String type){
    return typeSlotRepo.findAllByType(type);
  }

  public List<Slot> getAccommodationSlots(Event event) {
    return getAllSlots("accommodation", event);
  }
  public Slot findBySlotId(Long slotId) {
    return slotRepo.findBySlotId(slotId);
  }
  public Slot findByName(String name) {
    return slotRepo.findByName(name);
  }
  public TypeSlot findByTypeAndSlotName(String type, String slotName) {
    return typeSlotRepo.findByTypeAndSlot(
      type,
      slotRepo.findByName(slotName)
    );
  }

  public TypeSlot findByTypeSlotId(Long typeSlotId) {
    return typeSlotRepo.findByTypeSlotId(typeSlotId);
  }

  public TypeSlot findByTypeAndSlot(String type, Slot slot) {
    return typeSlotRepo.findByTypeAndSlot(
      type,
      slot
    );
  }

  public List<Slot> getAllSlots(Event event) {
    List<Slot> slots = new ArrayList<>();
    typeSlotRepo.findAll().forEach(
      typeSlot -> {
        if (eventHasTypeSlot(event, typeSlot)) {
          slots.add(typeSlot.getSlot());
        }
      }
    );
    slots.sort(Comparator.comparing(Slot::getSlotId));
    return slots;
  }

  public List<Slot> getAllSlots(String type, Event event) {
    List<Slot> slots = new ArrayList<>();
    typeSlotRepo.findAllByType(type).forEach(
      typeSlot -> {
        if (eventHasTypeSlot(event, typeSlot)) {
          slots.add(typeSlot.getSlot());
        }
      }
    );
    slots.sort(Comparator.comparing(Slot::getSlotId));
    return slots;
  }

  public List<Slot> getAllSlots(String type) {
    return typeSlotRepo.findAllByType(type).stream()
      .filter(typeSlot -> typeSlot.getType().equals(type))
      .map(TypeSlot::getSlot)
//      .sorted(Comparator.comparing(Slot::getSeqNr))
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getAllSlots() {
    List<Map<String, String>> slots = new ArrayList<>();

    slotRepo.findAll().forEach(slot -> {
      Map<String, String> slotData = slot.dataMap();
      slots.add(slotData);
    });

    return slots;
  }

  public boolean existsEventSlotType(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.existsByEventAndTypeSlot(event, typeSlot);
  }

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

  public List<Long> typeSlotIds(String type) {
    return typeSlotRepo.findAllByType(type).stream()
      .map(TypeSlot::getSlot)
      .sorted(Comparator.comparing(Slot::getSeqNr))
      .map(Slot::getSlotId)
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> typeSlotDataMap(String type) {
    List<Map<String, String>> slotMap = new ArrayList<>();

    typeSlotRepo.findAllByType(type).forEach(typeSlot -> {
      Map<String, String> typeSlotDataMap = typeSlot.getSlot().dataMap();
      slotMap.add(typeSlotDataMap);
    });

    return slotMap;
  }

  public List<Map<String, String>> getSlotSchema() {
    return Slot.schema();
  }
  public List<Map<String, String>> getSlotData() {
    List<Map<String, String>> slotsData = new ArrayList<>();
    slotRepo.findAll().forEach(slot -> {
      // discount data
      Map<String, String> slotData = slot.dataMap();
      slotsData.add(slotData);
    });
    return slotsData;
  }

  public void delete(JsonObject request) {
    Long slotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    delete(findBySlotId(slotId));
  }

  public boolean isUsed(Slot slot) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      event.getEventTypeSlots().forEach(eventTypeSlot -> {
        if (eventTypeSlot.getTypeSlot().getSlot().equals(slot)) {
          isUsed.set(true);
        }
      });
    });
    return isUsed.get();
  }

  public Field getField(String key) {
    Field field;
    try {
      field = Slot.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void update(JsonObject request) throws IOException, TemplateException {
    Long id = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Slot slot;

    if (id == null || id == 0) {
      slot = new Slot();
    } else {
      slot = findBySlotId(id);
    }

    Slot.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch(type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
                field =  getField(key);
                field.set(slot, outTextKey);
              }

              break;
            case "text":
              field = getField(key);
              field.set(slot, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(slot, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(slot, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(slot, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "bool":
              field = getField(key);
              field.set(slot, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(slot);
  }

  public String slotNames(List<Slot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }
    // Sort the list by seqNr before processing
    slots.sort(Comparator.comparing(Slot::getSeqNr));

    return slots.stream()
      .map(slot -> outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText())
      .collect(Collectors.joining(", "));
  }

  public String slotNames(Set<Slot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }

    // Convert the Set to a List
    return slotNames(slots.stream().collect(Collectors.toList()), language);
  }

  public void save (EventTypeSlot eventTypeSlot) {
    this.eventTypeSlotRepo.save(eventTypeSlot);
  }
}
