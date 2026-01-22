package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FoodRegistrationService {
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final FoodService foodService;
  private final SlotService slotService;
  private final WorkflowStatusService workflowStatusService;
  private final OutTextService outTextService;
  private final SpecialRegistrationRepo specialRegistrationRepo;
  private final DiscountRegistrationRepo discountRegistrationRepo;

  public void save(Registration registration, Food food, Slot slot) {
    foodRegistrationRepo.save(
      new FoodRegistration(
        registration,
        food,
        slot
      )
    );
  }

  public Set<Registration> getRegistrationsByEvent(Event event) {
    List<FoodRegistration> foodRegistrations
      = foodRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);

    Set<Registration> registrations = foodRegistrations.stream()
      .map(FoodRegistration::getRegistration)
      .filter(Registration::getActive)
      .collect(Collectors.toSet());

    return registrations;
  }

  public Boolean hasRegistrations(Food food, Boolean active) {
    return foodRegistrationRepo
      .findAllByRegistrationActive(active)
      .stream()
      .anyMatch(fr -> fr.getFood().equals(food));
  }
  public Boolean hasRegistrations(Event event, Food food, Boolean active) {
    return foodRegistrationRepo
      .findAllByRegistrationEventAndRegistrationActive(event, active)
      .stream()
      .anyMatch(fr -> fr.getFood().equals(food));
  }

  public String getReportFoodSlots(Registration registration) {
    Map<String, StringBuilder> merged = new HashMap<>();
    foodRegistrationRepo.findAllByRegistration(registration).forEach(fr -> {
      List<Map<String, String>> slotOutText =
        outTextService.getOutTextMapByKey(fr.getSlot().getName());
      if (slotOutText == null || slotOutText.isEmpty()) return;
      Map<String, String> map = slotOutText.get(0); // your structure

      map.forEach((lang, text) -> {
        merged
          .computeIfAbsent(lang, k -> new StringBuilder())
          .append(merged.get(lang).length() == 0 ? "" : ", ")
          .append(text);
      });
    });
    return merged.isEmpty()? "" : merged.toString();
  }

  public List<FoodRegistration> foodRegistrations(Registration registration, JsonObject foodRegistrationRequest) {
    List<FoodRegistration> foodRegistrations = new ArrayList<>();

    if (foodRegistrationRequest.get("foodRegistration") != null
      && !foodRegistrationRequest.get("foodRegistration").getAsJsonArray().isEmpty()) {
      JsonArray foodSlotsJson = foodRegistrationRequest.get("foodRegistration")
        .getAsJsonArray().get(0)
        .getAsJsonObject().get("food")
        .getAsJsonArray();

      foodSlotsJson.forEach(foodSlot -> {
        foodRegistrations.add(
          new FoodRegistration(
            registration,
            foodService.findByFoodId(foodSlot.getAsJsonObject().get("food").getAsJsonObject().get("foodId").getAsLong()),
            slotService.findBySlotId(foodSlot.getAsJsonObject().get("slot").getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
    return foodRegistrations;
  }

  public int countFoodSlotSubmitted(Food food, Slot slot, Event event){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getSubmitted(), event
    );
  }
  public int countFoodSlotConfirming(Food food, Slot slot, Event event){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getConfirming(), event
    );
  }
  public int countFoodSlotDone(Food food, Slot slot, Event event){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getDone(), event
    );
  }
  public int countFoodSlotConfirmingAndDone(Food food, Slot slot, Event event) {
    return countFoodSlotConfirming(food, slot, event) + countFoodSlotDone(food, slot, event);
  }
  public int countFoodSlotSubmittedConfirmingAndDone(Food food, Slot slot, Event event) {
    return countFoodSlotSubmitted(food, slot, event) + countFoodSlotConfirming(food, slot, event) + countFoodSlotDone(food, slot, event);
  }

  private boolean hasFoodRegistration(List<FoodRegistration> foodRegistrations, Food food, Slot slot) {
    AtomicBoolean hasFoodRegistration = new AtomicBoolean(false);
    foodRegistrations.forEach(foodRegistration -> {
      if (foodRegistration.getFood() == food && foodRegistration.getSlot() == slot) {
        hasFoodRegistration.set(true);
      }
    });

    return hasFoodRegistration.get();
  }

  public void updateFoodRegistrationRequest(Registration registration, JsonObject request) {
    List<FoodRegistration> requestFoodRegistrations = foodRegistrations(registration, request);
    List<FoodRegistration> foodRegistrations = foodRegistrationRepo.findAllByRegistration(registration);

    // delete in current if not in request
    foodRegistrations.forEach(foodRegistration -> {
      // log.info("foodRegistrations: " + foodRegistration.getFood().getName());

      // log.info("foodRegistrations, is in other list?: " + hasFoodRegistration(requestFoodRegistrations, foodRegistration.getFood(), foodRegistration.getSlot()));
      if (!hasFoodRegistration(requestFoodRegistrations, foodRegistration.getFood(), foodRegistration.getSlot())){
        foodRegistrationRepo.deleteAllByRegistrationAndFoodAndSlot(registration, foodRegistration.getFood(), foodRegistration.getSlot());
      }
    });

    // add new from request
    requestFoodRegistrations.forEach(foodRegistration -> {
      // log.info("requestFoodRegistrations: " + foodRegistration.getFood().getName());
      // log.info("requestFoodRegistrations, is in other list?: " + hasFoodRegistration(foodRegistrations, foodRegistration.getFood(), foodRegistration.getSlot()));
      if (!hasFoodRegistration(foodRegistrations, foodRegistration.getFood(), foodRegistration.getSlot())){
        save(registration, foodRegistration.getFood(), foodRegistration.getSlot());
      }
    });
  }

  public List<FoodRegistration> getAllByRegistration(Registration registration) {
    return foodRegistrationRepo.findAllByRegistration(registration);
  }

  public List<String> countFoodSlotSubmittedAsList(Food food, Slot slot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotSubmitted(food, slot, event)));
    return foodList;

  }
  public List<String>  countFoodSlotConfirmingAsList(Food food, Slot slot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotConfirming(food, slot, event)));
    return foodList;
  }
  public List<String> countFoodSlotDoneAsList(Food food, Slot slot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotDone(food, slot, event)));
    return foodList;
  }
  public List<String> countFoodSlotSubmittedConfirmingAndDoneAsList(Food food, Slot slot, Event event) {
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotSubmitted(food, slot, event) + countFoodSlotConfirming(food, slot, event) + countFoodSlotDone(food, slot, event)));
    return foodList;
  }
}
