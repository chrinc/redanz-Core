package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistrationId;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FoodRegistrationService {
  private final FoodRegistrationRepo foodRegistrationRepo;

  public void saveFoodRegistration(Registration registration, JsonArray foodRegistration) {
    JsonArray foodSlotsJson = foodRegistration.get(0).getAsJsonObject().get("food").getAsJsonArray();
    foodSlotsJson.forEach(foodSlot -> {
      foodRegistrationRepo.save(
        new FoodRegistration(
          new FoodRegistrationId(
            registration.getRegistrationId(),
            foodSlot.getAsJsonObject().get("food").getAsJsonObject().get("foodId").getAsLong(),
            foodSlot.getAsJsonObject().get("slot").getAsJsonObject().get("slotId").getAsLong()
          )
        )
      );
    });
  }

  public List<FoodRegistration> getAllByRegistration(Registration registration) {
    return foodRegistrationRepo.findAllByFoodRegistrationIdRegistrationId(registration.getRegistrationId());
  }
}
