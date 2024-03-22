package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
  private final EventService eventService;
  private final OutTextService outTextService;

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

  public String getReportFoodSlots(Registration registration, Language language) {
    AtomicReference<String> slots = new AtomicReference<>();
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      String slotOutText = outTextService.getOutTextByKeyAndLangKey(foodRegistration.getSlot().getName(), language.getLanguageKey()).getOutText();
      String foodOutText = outTextService.getOutTextByKeyAndLangKey(foodRegistration.getFood().getName(), language.getLanguageKey()).getOutText();
      if (slots.get() == null)
        slots.set(slotOutText);
      else {
        slots.set(slots.get() + ", " + slotOutText);
      }
    });
    return slots.get() == null ? "" : slots.toString();
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
