package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.SlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.TypeSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  TypeSlotRepo typeSlotRepo;
  FoodService foodService;
  SlotRepo slotRepo;
  EventTypeSlotRepo eventTypeSlotRepo;
  OutTextService outTextService;
  public void save(Slot slot) {
    slotRepo.save(slot);
  }

  public void save(TypeSlot typeSlot) {
    typeSlotRepo.save(typeSlot);
  }

  public List<Slot> getAllVolunteerSlots(Event event) {
    return getAllSlots("volunteer", event);
  }

  public List<Object> getFoodSlots(Event event) {
    List<Object> foodSlots = new ArrayList<>();
    typeSlotRepo.findAllByType("food").forEach(
      typeSlot -> {
        HashMap<String, Object> foodSlot = new HashMap<>();
        foodSlot.put("food", foodService.findByFoodId(typeSlot.getTypeObjectId()));
        foodSlot.put("slot", typeSlot.getSlot());
        if (eventHasTypeSlot(event, typeSlot)) {
          foodSlots.add(foodSlot);
        }
      });
    return foodSlots;
  }
  public boolean eventHasTypeSlot(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.findAllByEventAndTypeSlot(event, typeSlot).isPresent();
  }
  public List<List<Object>> getFoodSlotsPairsByEvent(Event event) {
    List<List<Object>> foodSlots = new ArrayList<>();
    List<EventTypeSlot> eventTypeSlots;
    eventTypeSlots = eventTypeSlotRepo.findAllByEvent(event);
    eventTypeSlots.forEach(eventTypeSlot -> {
      if (eventTypeSlot.getTypeSlot().getType().equals("food")) {
        List<Object> foodSlot = new ArrayList<>();
        foodSlot.add(foodService.findByFoodId(eventTypeSlot.getTypeSlot().getTypeObjectId()));
        foodSlot.add(eventTypeSlot.getTypeSlot().getSlot());
        foodSlots.add(foodSlot);
      }
    });
    return foodSlots;
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
  public TypeSlot findByTypeAndSlotName(String type, String slot) {
    return typeSlotRepo.findByTypeAndSlot(
      type,
      slotRepo.findByName(slot)
    );
  }

  private List<Slot> getAllSlots(String type, Event event) {
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
}
