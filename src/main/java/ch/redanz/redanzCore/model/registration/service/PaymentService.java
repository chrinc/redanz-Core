package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.config.DiscountConfig;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final FoodService foodService;
  private final DonationRegistrationRepo donationRegistrationRepo;
  private final RegistrationService registrationService;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;
  private final DiscountService discountService;
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final PersonService personService;
  private final UserService userService;
  private final EventService eventService;
  private final  RegistrationEmailService registrationEmailService;

  public synchronized boolean awaitPaymentConfirmation(Registration registration) throws InterruptedException, TimeoutException {
    long timeout = 1000L * 60 * 5; // 5 minutes
    long waitTime = 2000;
    long t0 = System.currentTimeMillis() + timeout;
    while (!checkPaymentConfirmed(registration)) {
      long delay = t0 - System.currentTimeMillis();
      if (delay < 0) {
        throw new TimeoutException();
      }
      wait(waitTime);
    }
    return true;
  }

  private synchronized boolean checkPaymentConfirmed(Registration registration) {
    return
      workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration)
        .getWorkflowStatus().equals(workflowStatusService.getDone());
  }

  public PaymentDetailsResponse getPaymentDetails(Registration registration) {
    HashMap<String, Double> items = new HashMap<>();
    HashMap<String, Double> discounts = new HashMap<>();

    // pass
    items.put(registration.getBundle().getName(), registration.getBundle().getPrice());

    // food
    foodRegistrationRepo.findAllByFoodRegistrationIdRegistrationId(registration.getRegistrationId()).forEach(foodRegistration -> {
      Food foodItem = foodService.findByFoodId(foodRegistration.getFoodRegistrationId().getFoodId());
      items.put(
        foodItem.getName(),
        foodItem.getPrice()
      );
    });

    // donation
    if (donationRegistrationRepo.findByRegistration(registration) != null) {
      items.put(OutTextConfig.LABEL_DONATION_EN.getOutTextKey(), donationRegistrationRepo.findByRegistration(registration).getAmount());
    }

    // discounts
    // @Todo: Set Early Bird Constants
    if (registrationService.findAllByCurrentEventAndWorkflowStatus(
      workflowStatusService.getDone()
    ).size() < 50) {
      discounts.put(DiscountConfig.EARLY_BIRD.getName(), discountService.findByName(DiscountConfig.EARLY_BIRD.getName()).getDiscount());
    }
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      discounts.put(discountRegistration.getDiscount().getName(), discountRegistration.getDiscount().getDiscount());
    });

    Double totalAmount =
      (items.values().stream().mapToDouble(Double::doubleValue).sum()
        - discounts.values().stream().mapToDouble(Double::doubleValue).sum());
    return
      new PaymentDetailsResponse(
        items,
        discounts,
        totalAmount
      );
  }
  public void onPaymentReceived(Long userId) throws IOException, TemplateException {
    workflowTransitionService.setWorkflowStatus(
      registrationService.findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.findByName(EventConfig.EVENT2022.getName())
      ).get(),
      workflowStatusService.getDone()
    );
    registrationEmailService.sendEmailBookingConfirmation(personService.findByUser(userService.findByUserId(userId)));
  }
  public void onPaymentConfirmed(JsonObject request) throws IOException, TemplateException {
    JsonObject transaction = request.get("transaction").getAsJsonObject();
    log.info("inc@onPaymentConfirmed, transaction: {}", transaction);
    Long userId = transaction.get("referenceId").getAsLong();
    Number amount = transaction.get("invoice").getAsJsonObject().get("amount").getAsNumber();
    log.info("inc@onPaymentConfirmed, userId: {}", userId);
    log.info("inc@onPaymentConfirmed, amount: {}", amount);
    //    @todo check amount first
    onPaymentReceived(userId);
  }
}
