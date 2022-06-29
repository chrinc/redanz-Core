package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.Food;
import ch.redanz.redanzCore.model.workshop.config.DiscountConfig;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

//  private boolean arrived;

  public synchronized boolean awaitPaymentConfirmation(Registration registration) throws InterruptedException, TimeoutException {
    long timeout = 1000L * 60 * 10; // 10 minutes
    long waitTime = 2000;
    long t0 = System.currentTimeMillis() + timeout;
    while (!checkPaymentConfirmed(registration)) {
      long delay = t0 - System.currentTimeMillis();
      log.info("delay: {}", delay);
      if (delay < 0) {
        log.info("throw ?");
        throw new TimeoutException();
      }
      log.info("inc, wait?");
      wait(waitTime);
    }
    log.info("return ?");
    return true;
  }

  private synchronized boolean checkPaymentConfirmed(Registration registration) {
    return
      workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration)
        .getWorkflowStatus().equals(workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.DONE.getName()));
  }

  public PaymentDetailsResponse getPaymentDetails(Registration registration) {
//    User user = userService.findByUserId(userId);
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
      workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.DONE.getName())
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
}
