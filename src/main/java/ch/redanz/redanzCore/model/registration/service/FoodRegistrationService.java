package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistrationId;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FoodRegistrationService {
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final FoodService foodService;
  private final SlotService slotService;

  public void save(Registration registration, Food food, Slot slot) {
    foodRegistrationRepo.save(
      new FoodRegistration(
        new FoodRegistrationId(
          registration.getRegistrationId(),
          food.getFoodId(),
          slot.getSlotId()
        )
      )
    );
  }

  public void saveFoodRegistration(Registration registration, JsonArray foodRegistration) {
    JsonArray foodSlotsJson = foodRegistration.get(0).getAsJsonObject().get("food").getAsJsonArray();
    foodSlotsJson.forEach(foodSlot -> {
      save(
        registration,
        foodService.findByFoodId(foodSlot.getAsJsonObject().get("food").getAsJsonObject().get("foodId").getAsLong()),
        slotService.findBySlotId(foodSlot.getAsJsonObject().get("slot").getAsJsonObject().get("slotId").getAsLong())
      );
    });
  }

  public List<FoodRegistration> getAllByRegistration(Registration registration) {
    return foodRegistrationRepo.findAllByFoodRegistrationIdRegistrationId(registration.getRegistrationId());
  }
}
