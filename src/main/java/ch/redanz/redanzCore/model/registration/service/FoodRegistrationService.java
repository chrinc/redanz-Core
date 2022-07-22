package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
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
  private final WorkflowStatusService workflowStatusService;
  private final EventService eventService;

  public void save(Registration registration, Food food, Slot slot) {
    foodRegistrationRepo.save(
      new FoodRegistration(
        registration,
        food,
        slot
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

  public int countFoodReleasedAndDone(Food food) {
    return foodRegistrationRepo.countAllByFoodAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
    )
      +
      foodRegistrationRepo.countAllByFoodAndRegistrationWorkflowStatusAndRegistrationEvent(
       food, workflowStatusService.getDone(), eventService.getCurrentEvent()
      );
  }

  public int countFoodSlotSubmitted(Food food, Slot slot){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getSubmitted(), eventService.getCurrentEvent()
    );
  }
  public int countFoodSlotConfirming(Food food, Slot slot){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
    );
  }
  public int countFoodSlotDone(Food food, Slot slot){
    return foodRegistrationRepo.countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      food, slot, workflowStatusService.getDone(), eventService.getCurrentEvent()
    );
  }
  public int countFoodSlotConfirmingAndDone(Food food, Slot slot) {
    return countFoodSlotConfirming(food, slot) + countFoodSlotDone(food, slot);
  }

  public int countFoodSlotSubmittedReleasedAndDone(Food food, Slot slot) {
    return countFoodSlotSubmitted(food, slot) + countFoodSlotConfirming(food, slot) + countFoodSlotDone(food, slot);
  }

  public List<FoodRegistration> getAllByRegistration(Registration registration) {
    return foodRegistrationRepo.findAllByRegistration(registration);
  }
}
