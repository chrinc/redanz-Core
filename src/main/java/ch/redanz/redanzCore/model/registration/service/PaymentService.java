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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentService {
  private final FoodRegistrationRepo foodRegistrationRepo;
  private final DonationRegistrationRepo donationRegistrationRepo;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final PrivateClassService privateClassService;
  private final SpecialRegistrationService specialRegistrationService;
  private final EventService eventService;
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationPaymentRepo registrationPaymentRepo;
  private final EventDiscountRepo eventDiscountRepo;


  public synchronized boolean awaitPaymentConfirmation(Registration registration) throws InterruptedException, TimeoutException {
    // Timout 4 Minutes, gateway timeout should be 5 minutes
    long timeout = 1000L * 60 * 4;
    // testing
    // long timeout = 1000L * 3;
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
      double price = specialRegistration.getEventSpecial().getPrice();
      totalAmount.addAndGet((int) price);
      specials.add(
        List.of(
          specialRegistration.getEventSpecial().getName(),
          specialRegistration.getEventSpecial().getDescription(),
          String.valueOf((int) price)
        )
      );
    });

    // private class
    privateClassService.findAllByRegistrations(registration).forEach(privateClassRegistration -> {
      EventPrivateClass eventPrivateClass = privateClassRegistration.getEventPrivateClass();
      totalAmount.addAndGet((int) eventPrivateClass.getPrice());

      privateClasses.add(
        List.of(
          eventPrivateClass.getName(),
          eventPrivateClass.getDescription(),
          String.valueOf((int) eventPrivateClass.getPrice())
        )
      );
    });

    // food
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      totalAmount.addAndGet((int) foodRegistration.getEventFoodSlot().getPrice());
      foodSlots.add(
        List.of(
          foodRegistration.getEventFoodSlot().getName(),
          foodRegistration.getEventFoodSlot().getName(),
          String.valueOf((int) foodRegistration.getEventFoodSlot().getPrice())
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
        int discount = (int) discountRegistration.getEventDiscount().getDiscountAmount();
        totalAmount.addAndGet(discount * (-1));
        discounts.add(
          List.of(
            discountRegistration.getEventDiscount().getName(),
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
      totalAmount.addAndGet((int) specialRegistration.getEventSpecial().getPrice());
    });

    // private class
    privateClassService.findAllByRegistrations(registration).forEach(privateClassRegistration -> {
      totalAmount.addAndGet((int) privateClassRegistration.getEventPrivateClass().getPrice());
    });

    // food
    foodRegistrationRepo.findAllByRegistration(registration).forEach(foodRegistration -> {
      totalAmount.addAndGet((int) foodRegistration.getEventFoodSlot().getPrice());
    });

    // donation
    if (donationRegistrationRepo.findByRegistration(registration) != null) {
      totalAmount.addAndGet((int) donationRegistrationRepo.findByRegistration(registration).getAmount());
    }

    // discount
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      int discount = (int) discountRegistration.getEventDiscount().getDiscountAmount();
      totalAmount.addAndGet(discount * (-1));
    });

    return totalAmount.get();
  };

  public long amountPaid(Registration registration){
    return registrationPaymentRepo.sumAmountByRegistration(registration);
  }

  public long amountDue(Registration registration) {
    long amountPaid = amountPaid(registration);
    long totalAmount = totalAmount(registration);
    return totalAmount - amountPaid;
  }

  public void updatePayment(Registration registration, Long amount) {
    registrationPaymentRepo.save(
      new RegistrationPayment(registration, amount, LocalDateTime.now())
    );
  }

  public void onPaymentReceived(Registration registration, Long amount) throws IOException, TemplateException {

      // Update payment stats
      updatePayment(registration, amount);

      // Update workflow
     if (amountDue(registration) == 0) {

       workflowTransitionService.setWorkflowStatus(
         registration,
         workflowStatusService.getDone()
       );

       // Email confirmation
       registrationEmailService.sendEmailBookingConfirmation(
         registration.getParticipant(),
         registrationEmailService.findByRegistration(registration),
         registration,
         getPaymentDetails(registration)
       );
     }
  }
  public void onPaymentConfirmed(Registration registration, Long amount, String status) throws IOException, TemplateException {
    //    @todo referenceId not found
    //    @todo check amount first
    //    @todo check payment method
    if (Objects.equals(status, "confirmed")) {
      onPaymentReceived(registration, amount);
    }
  }
}
