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
  private final WorkflowStatusService workflowStatusService;
  private final OutTextService outTextService;

  public void save(Registration registration, EventFoodSlot eventFoodSlot) {
    foodRegistrationRepo.save(
      new FoodRegistration(
        registration,
        eventFoodSlot
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

  public Boolean hasRegistrations(EventFoodSlot eventFoodSlot, Boolean active) {
    return foodRegistrationRepo
      .findAllByRegistrationActive(active)
      .stream()
      .anyMatch(fr -> fr.getEventFoodSlot().equals(eventFoodSlot));
  }
  public Boolean hasRegistrations(Event event, EventFoodSlot eventFoodSlot, Boolean active) {
    return foodRegistrationRepo
      .findAllByRegistrationEventAndRegistrationActive(event, active)
      .stream()
      .anyMatch(fr -> fr.getEventFoodSlot().equals(eventFoodSlot));
  }

  public String getReportFoodSlots(Registration registration) {
    Map<String, StringBuilder> merged = new HashMap<>();
    foodRegistrationRepo.findAllByRegistration(registration).forEach(fr -> {
      List<Map<String, String>> slotOutText =
        outTextService.getOutTextMapByKey(fr.getEventFoodSlot().getName());
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
            foodService.findEventFoodSlotById(foodSlot.getAsJsonObject().get("eventFoodSlotId").getAsLong())
          )
        );
      });
    }
    return foodRegistrations;
  }

  public int countFoodSlotSubmitted(EventFoodSlot eventFoodSlot, Event event){
    return foodRegistrationRepo.countAllByEventFoodSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      eventFoodSlot, workflowStatusService.getSubmitted(), event
    );
  }
  public int countFoodSlotConfirming(EventFoodSlot eventFoodSlot, Event event){
    return foodRegistrationRepo.countAllByEventFoodSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      eventFoodSlot, workflowStatusService.getConfirming(), event
    );
  }
  public int countFoodSlotDone(EventFoodSlot eventFoodSlot, Event event){
    return foodRegistrationRepo.countAllByEventFoodSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
      eventFoodSlot, workflowStatusService.getDone(), event
    );
  }
  public int countFoodSlotConfirmingAndDone(EventFoodSlot eventFoodSlot, Event event) {
    return countFoodSlotConfirming(eventFoodSlot, event) + countFoodSlotDone(eventFoodSlot, event);
  }
  public int countFoodSlotSubmittedConfirmingAndDone(EventFoodSlot eventFoodSlot, Event event) {
    return countFoodSlotSubmitted(eventFoodSlot, event) + countFoodSlotConfirming(eventFoodSlot, event) + countFoodSlotDone(eventFoodSlot, event);
  }

  private boolean hasFoodRegistration(List<FoodRegistration> foodRegistrations, EventFoodSlot eventFoodSlot) {
    AtomicBoolean hasFoodRegistration = new AtomicBoolean(false);
    foodRegistrations.forEach(foodRegistration -> {
      if (foodRegistration.getEventFoodSlot() == eventFoodSlot) {
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
      if (!hasFoodRegistration(requestFoodRegistrations, foodRegistration.getEventFoodSlot())){
        foodRegistrationRepo.deleteAllByRegistrationAndEventFoodSlot(registration, foodRegistration.getEventFoodSlot());
      }
    });

    // add new from request
    requestFoodRegistrations.forEach(foodRegistration -> {
      if (!hasFoodRegistration(foodRegistrations, foodRegistration.getEventFoodSlot())){
        save(registration, foodRegistration.getEventFoodSlot());
      }
    });
  }

  public List<FoodRegistration> getAllByRegistration(Registration registration) {
    return foodRegistrationRepo.findAllByRegistration(registration);
  }

  public List<String> countFoodSlotSubmittedAsList(EventFoodSlot eventFoodSlot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotSubmitted(eventFoodSlot, event)));
    return foodList;

  }
  public List<String>  countFoodSlotConfirmingAsList(EventFoodSlot eventFoodSlot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotConfirming(eventFoodSlot, event)));
    return foodList;
  }
  public List<String> countFoodSlotDoneAsList(EventFoodSlot eventFoodSlot, Event event){
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotDone(eventFoodSlot, event)));
    return foodList;
  }
  public List<String> countFoodSlotSubmittedConfirmingAndDoneAsList(EventFoodSlot eventFoodSlot, Event event) {
    List<String> foodList = new ArrayList<>();
    foodList.add(String.valueOf(countFoodSlotSubmitted(eventFoodSlot, event) + countFoodSlotConfirming(eventFoodSlot, event) + countFoodSlotDone(eventFoodSlot, event)));
    return foodList;
  }
}
