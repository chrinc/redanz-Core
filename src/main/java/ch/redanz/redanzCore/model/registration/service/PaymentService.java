package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.*;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

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
  private final RegistrationEmailService registrationEmailService;
  private final SlotService slotService;
  private final BundleService bundleService;


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
    List<List<String>> items = new ArrayList<>();
    List<List<String>> foodSlots = new ArrayList<>();
    List<List<String>> donation = new ArrayList<>();
    List<List<String>> discounts = new ArrayList<>();

    AtomicInteger totalAmount = new AtomicInteger();
    totalAmount.set((int) registration.getBundle().getPrice());

    // pass
    items.add(
      List.of(
        registration.getBundle().getName(),
        String.valueOf((int) registration.getBundle().getPrice())
      )
    );

    // food
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      totalAmount.addAndGet((int) foodRegistration.getFood().getPrice());
      foodSlots.add(
        List.of(
          foodRegistration.getFood().getName(),
          foodRegistration.getSlot().getName(),
          String.valueOf((int) foodRegistration.getFood().getPrice())
        )
      );
    });

    // donation
    if (donationRegistrationRepo.findByRegistration(registration) != null) {
      int donationAmount = (int) donationRegistrationRepo.findByRegistration(registration).getAmount();
      totalAmount.addAndGet(donationAmount);
      log.info("inc, donationAmount: {}", donationAmount);
      log.info("inc, String.valueOf(donationAmount): {}", String.valueOf(donationAmount));
      donation.add(
        List.of(
          OutTextConfig.LABEL_DONATION_EN.getOutTextKey(),
          String.valueOf(donationAmount)
        )
      );
    }
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      int discount = (int) discountRegistration.getDiscount().getDiscount();
      totalAmount.addAndGet(discount * (-1));
      discounts.add(
        List.of(
          discountRegistration.getDiscount().getName(),
          String.valueOf(discount)
        )
      );
    });

    return
      new PaymentDetailsResponse(
        items,
        foodSlots,
        donation,
        discounts,
        totalAmount
      );
  }
  public void onPaymentReceived(Long userId) throws IOException, TemplateException {
    Registration registration = registrationService.findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      eventService.findByName(EventConfig.EVENT2022.getName())
    ).get();

    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getDone()
    );
    registrationEmailService.sendEmailBookingConfirmation(
      personService.findByUser(userService.findByUserId(userId)),
      registrationEmailService.findByRegistration(registration)
    );
    registrationService.updateSoldOut();
  }
  public void onPaymentConfirmed(JsonObject request) throws IOException, TemplateException {
    JsonObject transaction = request.get("transaction").getAsJsonObject();
    Long userId = transaction.get("referenceId").getAsLong();

    //    Number amount = transaction.get("invoice").getAsJsonObject().get("amount").getAsNumber();
    //    log.info("inc@onPaymentConfirmed, userId: {}", userId);
    //    log.info("inc@onPaymentConfirmed, amount: {}", amount);
    //    @todo check amount first
    onPaymentReceived(userId);
  }
}
