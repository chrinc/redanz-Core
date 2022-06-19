package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Slot;
import ch.redanz.redanzCore.model.workshop.repository.SlotRepo;
import ch.redanz.redanzCore.model.workshop.repository.TypeSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  
  TypeSlotRepo typeSlotRepo;
  FoodService foodService;
  SlotRepo slotRepo;

  public List<Slot> getAllVolunteerSlots() {
    return getAllSlots("volunteer");
  }

  public List<Object> getAllFoodSlots() {
    List<Object> foodSlots = new ArrayList<>();

    typeSlotRepo.findAllByType("food").forEach(
      typeSlot -> {
        HashMap<String, Object> foodSlot = new HashMap<>();
        foodSlot.put("food", foodService.findByFoodId(typeSlot.getTypeObjectId()));
        foodSlot.put("slot", typeSlot.getSlot());

        foodSlots.add(foodSlot);
      });

    return foodSlots;
  }

  public List<Slot> getAllAccommodationSlots() {
    return getAllSlots("accommodation");
  }
  public Slot findBySlotId(Long slotId) {
    return slotRepo.findBySlotId(slotId);
  }

  private List<Slot> getAllSlots(String type) {

    List<Slot> slots = new ArrayList<>();
    typeSlotRepo.findAllByType(type).forEach(
      typeSlot -> slots.add(typeSlot.getSlot())
    );
    return slots;

  }
}
