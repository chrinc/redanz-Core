package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.SlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.TypeSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  TypeSlotRepo typeSlotRepo;
  SlotRepo slotRepo;
  EventTypeSlotRepo eventTypeSlotRepo;
  OutTextService outTextService;

  public void save(Slot slot) {
    slotRepo.save(slot);
  }

  public boolean foodSlotExists(
    Slot slot, Food food
  ) {
    return  typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot("food", food.getFoodId(), slot);
  }

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
      log.info("inc after delete Slot: {}", slot.getSlotId());
    } catch (Exception exception){
      log.info("inc catches exception: {}", exception);
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
      .sorted(Comparator.comparing(Slot::getSeqNr))
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

  public void updateTypeSlot(String type, Slot slot, Long typeObjectId) {
    if (!typeSlotRepo.existsByTypeAndTypeObjectIdAndSlot(type, typeObjectId, slot)) {
      typeSlotRepo.save(
        new TypeSlot(
          type,
          slot,
          typeObjectId
        )
      );
    }
  };

  public void save (EventTypeSlot eventTypeSlot) {
    this.eventTypeSlotRepo.save(eventTypeSlot);
  }
}
