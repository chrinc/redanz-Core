package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationPayment;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.FoodRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.RegistrationPaymentRepo;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

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
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final PrivateClassService privateClassService;
  private final SpecialRegistrationService specialRegistrationService;
  private final EventService eventService;
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationPaymentRepo registrationPaymentRepo;
  private final BundleService bundleService;
  private final EventDiscountRepo eventDiscountRepo;


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
    List<List<String>> specials = new ArrayList<>();
    List<List<String>> privateClasses = new ArrayList<>();
    List<List<String>> foodSlots = new ArrayList<>();
    List<List<String>> donation = new ArrayList<>();
    List<List<String>> discounts = new ArrayList<>();

    AtomicLong totalAmount = new AtomicLong();
    totalAmount.set((int) registration.getBundle().getPrice());

    // pass
    items.add(
      List.of(
        registration.getBundle().getName(),
        String.valueOf((int) registration.getBundle().getPrice())
      )
    );

    // specials
    specialRegistrationService.findAllByRegistration(registration).forEach(specialRegistration -> {
      Special special = specialRegistration.getSpecial();
      double price = 0.0;
//      log.info(String.valueOf(eventService.hasEventSpecial(specialRegistration.getRegistration().getEvent(), special)));
      if (eventService.hasEventSpecial(specialRegistration.getRegistration().getEvent(), special)) {
        EventSpecial eventSpecial = eventService.findByEventAndSpecial(registration.getEvent(), special);
        price = eventSpecial.getPrice();
      }

      totalAmount.addAndGet((int) price);
      specials.add(
        List.of(
          special.getName(),
          special.getDescription(),
          String.valueOf((int) price)
        )
      );
//      log.info(String.valueOf(specials));
    });

    // private class
    privateClassService.findAllByRegistrations(registration).forEach(privateClassRegistration -> {
      PrivateClass privateClass = privateClassRegistration.getPrivateClass();
      EventPrivateClass eventPrivateClass = eventService.findByEventAndPrivate(registration.getEvent(), privateClass);
      totalAmount.addAndGet((int) eventPrivateClass.getPrice());

      privateClasses.add(
        List.of(
          privateClass.getName(),
          privateClass.getDescription(),
          String.valueOf(eventPrivateClass.getPrice())
        )
      );
    });

    // food
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      totalAmount.addAndGet((int) eventService.findEventFoodSlotByEventAndFood(
         registration.getEvent()
        ,foodRegistration.getFood()
        ,foodRegistration.getSlot()
      ).getPrice());
      foodSlots.add(
        List.of(
          foodRegistration.getFood().getName(),
          foodRegistration.getSlot().getName(),
          String.valueOf((int) eventService.findEventFoodSlotByEventAndFood(
            registration.getEvent()
            ,foodRegistration.getFood()
            ,foodRegistration.getSlot()
          ).getPrice())
        )
      );
    });

    // donation
    if (donationRegistrationRepo.findByRegistration(registration) != null) {
      int donationAmount = (int) donationRegistrationRepo.findByRegistration(registration).getAmount();
      totalAmount.addAndGet(donationAmount);
      donation.add(
        List.of(
          OutTextConfig.LABEL_DONATION_EN.getOutTextKey(),
          String.valueOf(donationAmount)
        )
      );
    }

    // discounts
    discountRegistrationRepo.findAllByRegistration(registration).forEach(
      discountRegistration -> {
        int discount = (int) eventDiscountRepo.findByEventAndDiscount(registration.getEvent(), discountRegistration.getDiscount()).getDiscountAmount();
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
        specials,
        privateClasses,
        foodSlots,
        donation,
        discounts,
        totalAmount(registration),
        amountPaid(registration),
        amountDue(registration),
        registration.getRegistrationId(),
        registration.getWorkflowStatus().getWorkflowStatusId() == workflowStatusService.getConfirming().getWorkflowStatusId()
      );
  }

  public long totalAmount(Registration registration){
    AtomicLong totalAmount = new AtomicLong();
    totalAmount.set((int) registration.getBundle().getPrice());

    // specials
    specialRegistrationService.findAllByRegistration(registration).forEach(specialRegistration -> {
      if (eventService.hasEventSpecial(registration.getEvent(), specialRegistration.getSpecial())) {
        totalAmount.addAndGet((int) eventService.findByEventAndSpecial(registration.getEvent(), specialRegistration.getSpecial()).getPrice());
      }
    });

    // private class
    privateClassService.findAllByRegistrations(registration).forEach(privateClassRegistration -> {
      totalAmount.addAndGet((int) eventService.findByEventAndPrivate(registration.getEvent(), privateClassRegistration.getPrivateClass()).getPrice());
    });

    // food
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      totalAmount.addAndGet((int) eventService.findEventFoodSlotByEventAndFood(
        registration.getEvent()
        ,foodRegistration.getFood()
        ,foodRegistration.getSlot()
      ).getPrice());
    });

    // donation
    if (donationRegistrationRepo.findByRegistration(registration) != null) {
      totalAmount.addAndGet((int) donationRegistrationRepo.findByRegistration(registration).getAmount());
    }

    // discount
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      int discount = (int) eventDiscountRepo.findByEventAndDiscount(registration.getEvent(), discountRegistration.getDiscount()).getDiscountAmount();
      totalAmount.addAndGet(discount * (-1));
    });

    return totalAmount.get();
  };

  public long amountPaid(Registration registration){
    return registrationPaymentRepo.sumAmountByRegistration(registration);
  }

  public long amountDue(Registration registration) {
    // log.info("amountDue");
    long amountPaid = amountPaid(registration);
    long totalAmount = totalAmount(registration);
    // log.info("amountPaid: " + amountPaid);
    // log.info("totalAmount: " + totalAmount);
    // log.info("amountDue: " + (totalAmount - amountPaid > 0 ? totalAmount - amountPaid : 0));
    return totalAmount - amountPaid;
  }

  public void updatePayment(Registration registration, Long amount) {
    registrationPaymentRepo.save(
      new RegistrationPayment(registration, amount, LocalDateTime.now())
    );
  }

  public void onPaymentReceived(Registration registration, Long amount) throws IOException, TemplateException {
    if (amount > 0) {

      // Update payment stats
      updatePayment(registration, amount);

      // Update workflow
      workflowTransitionService.setWorkflowStatus(
        registration,
        workflowStatusService.getDone()
      );

      // Email confirmation
      registrationEmailService.sendEmailBookingConfirmation(
        registration.getParticipant(),
        registrationEmailService.findByRegistration(registration),
        registration
      );

      // Update SoldOut stats
      registrationService.updateSoldOut(registration.getEvent());
    }
  }
  public void onPaymentConfirmed(JsonObject request) throws IOException, TemplateException {
    JsonObject transaction = request.get("transaction").getAsJsonObject();
    Long registrationId = transaction.get("referenceId").getAsLong();
    Registration registration = registrationService.findByRegistrationId(registrationId);

    // receive incl. raps
    Long amount = transaction.get("amount").getAsLong() / 100;

    //    @todo referenceId not found
    //    @todo check amount first
    //    @todo check payment method
    if (Objects.equals(transaction.get("status").getAsString(), "confirmed")) {
      onPaymentReceived(registration, amount);
    }
  }
}
