package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.PaymentStatus;
import ch.redanz.redanzCore.model.registration.entities.PaymentType;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationPayment;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationPaymentRepo registrationPaymentRepo;
  private final SpecialRegistrationRepo specialRegistrationRepo;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final RegistrationRepo registrationRepo;
  private final OutTextService outTextService;

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

  public void updateRegistrationPaymentStatus(Registration registration, PaymentStatus paymentStatus) {
    registration.setPaymentStatus(paymentStatus);
    registrationRepo.save(registration);
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

    // fees (e.g. cancellation fee) and refunds: [comment out text key or "", amount]
    List<List<String>> fees = new ArrayList<>();
    List<List<String>> refunds = new ArrayList<>();
    registrationPaymentRepo.findAllByRegistration(registration).stream()
      .filter(payment -> payment.getAmount() != null)
      .forEach(payment -> {
        List<String> booking = List.of(
          payment.getComment() == null ? "" : payment.getComment(),
          String.valueOf(payment.getAmount())
        );
        switch (payment.getType()) {
          case FEE -> fees.add(booking);
          case REFUND -> refunds.add(booking);
          default -> {}
        }
      });
    sumWithoutComment(fees);
    sumWithoutComment(refunds);

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
        registration.getWorkflowStatus().getWorkflowStatusId() == workflowStatusService.getConfirming().getWorkflowStatusId(),
        fees,
        refunds,
        refunds.stream().mapToLong(refund -> Long.parseLong(refund.get(1))).sum()
      );
  }

  // bookings without comment shown as one line (sum), bookings with comment separately
  private static void sumWithoutComment(List<List<String>> bookings) {
    long sum = bookings.stream()
      .filter(booking -> booking.get(0).isEmpty())
      .mapToLong(booking -> Long.parseLong(booking.get(1)))
      .sum();
    boolean withoutComment = bookings.removeIf(booking -> booking.get(0).isEmpty());
    if (withoutComment) {
      bookings.add(0, List.of("", String.valueOf(sum)));
    }
  }

  public long totalAmount(Registration registration){
    return totalAmount(
      registration,
      specialRegistrationService.findAllByRegistration(registration),
      privateClassService.findAllByRegistrations(registration),
      foodRegistrationRepo.findAllByRegistration(registration),
      donationRegistrationRepo.findByRegistration(registration),
      discountRegistrationRepo.findAllByRegistration(registration)
    );
  };

  // price of a registration from its booked items (loaded by the caller, e.g. for all registrations of an event at once)
  public long totalAmount(
    Registration registration,
    List<SpecialRegistration> specialRegistrations,
    List<PrivateClassRegistration> privateClassRegistrations,
    List<FoodRegistration> foodRegistrations,
    DonationRegistration donationRegistration,
    List<DiscountRegistration> discountRegistrations
  ){
    AtomicLong totalAmount = new AtomicLong();
    totalAmount.set((int) registration.getBundle().getPrice());

    // specials
    specialRegistrations.forEach(specialRegistration -> {
      totalAmount.addAndGet((int) specialRegistration.getEventSpecial().getPrice());
    });

    // private class
    privateClassRegistrations.forEach(privateClassRegistration -> {
      totalAmount.addAndGet((int) privateClassRegistration.getEventPrivateClass().getPrice());
    });

    // food
    foodRegistrations.forEach(foodRegistration -> {
      totalAmount.addAndGet((int) foodRegistration.getEventFoodSlot().getPrice());
    });

    // donation
    if (donationRegistration != null) {
      totalAmount.addAndGet((int) donationRegistration.getAmount());
    }

    // discount
    discountRegistrations.forEach(discountRegistration -> {
      int discount = (int) discountRegistration.getEventDiscount().getDiscountAmount();
      totalAmount.addAndGet(discount * (-1));
    });

    return totalAmount.get();
  };

  // payments minus refunds
  public long amountPaid(Registration registration){
    return paidAmount(registrationPaymentRepo.findAllByRegistration(registration));
  }

  private static long paidAmount(List<RegistrationPayment> payments) {
    return payments.stream()
      .filter(payment -> payment.getAmount() != null)
      .mapToLong(payment -> switch (payment.getType()) {
        case PAYMENT -> payment.getAmount();
        case REFUND -> -payment.getAmount();
        case FEE -> 0;
      })
      .sum();
  }

  // fees (e.g. cancellation fee) are charged in addition to the total (amount due)
  private static long feeAmount(List<RegistrationPayment> payments) {
    return payments.stream()
      .filter(payment -> payment.getAmount() != null && payment.getType() == PaymentType.FEE)
      .mapToLong(RegistrationPayment::getAmount)
      .sum();
  }

  public record Amounts(long totalAmount, long amountDue) {}

  // total and due amount of registrations of an event: the booked items and payments are loaded
  // once for the whole event instead of per registration (reports)
  public Map<Long, Amounts> amountsByRegistration(Event event, List<Registration> registrations) {
    Map<Long, List<SpecialRegistration>> specials = byRegistration(specialRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true), SpecialRegistration::getRegistration);
    Map<Long, List<PrivateClassRegistration>> privateClasses = byRegistration(privateClassRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true), PrivateClassRegistration::getRegistration);
    Map<Long, List<FoodRegistration>> foods = byRegistration(foodRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true), FoodRegistration::getRegistration);
    Map<Long, List<DonationRegistration>> donations = byRegistration(donationRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true), DonationRegistration::getRegistration);
    Map<Long, List<DiscountRegistration>> discounts = byRegistration(discountRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true), DiscountRegistration::getRegistration);
    Map<Long, List<RegistrationPayment>> payments = byRegistration(registrationPaymentRepo.findAllByRegistrationEventAndRegistrationActive(event, true), RegistrationPayment::getRegistration);

    Map<Long, Amounts> amounts = new HashMap<>();
    registrations.forEach(registration -> {
      Long registrationId = registration.getRegistrationId();
      List<DonationRegistration> donation = donations.getOrDefault(registrationId, List.of());
      List<RegistrationPayment> registrationPayments = payments.getOrDefault(registrationId, List.of());
      long totalAmount = totalAmount(
        registration,
        specials.getOrDefault(registrationId, List.of()),
        privateClasses.getOrDefault(registrationId, List.of()),
        foods.getOrDefault(registrationId, List.of()),
        donation.isEmpty() ? null : donation.get(0),
        discounts.getOrDefault(registrationId, List.of())
      );
      amounts.put(registrationId, new Amounts(totalAmount, totalAmount - paidAmount(registrationPayments) + feeAmount(registrationPayments)));
    });
    return amounts;
  }

  private static <T> Map<Long, List<T>> byRegistration(List<T> items, Function<T, Registration> registration) {
    return items.stream().collect(Collectors.groupingBy(item -> registration.apply(item).getRegistrationId()));
  }

  public long amountDue(Registration registration) {
    long amountPaid = amountPaid(registration);
    long totalAmount = totalAmount(registration);
    return totalAmount - amountPaid + feeAmount(registrationPaymentRepo.findAllByRegistration(registration));
  }

  public void updatePayment(Registration registration, Long amount) {
    registrationPaymentRepo.save(
      new RegistrationPayment(registration, amount, LocalDateTime.now())
    );
  }

  // payment, refund or fee entered by an organizer, comment: optional label {EN, GE}
  public void addBooking(Registration registration, PaymentType type, Long amount, JsonObject comment) {
    registrationPaymentRepo.save(
      new RegistrationPayment(
        registration,
        amount,
        LocalDateTime.now(),
        type,
        commentLabel(comment)
      )
    );
  }

  // payments, refunds and fees of a registration, for the organizer
  public List<Map<String, Object>> bookings(Registration registration) {
    return registrationPaymentRepo.findAllByRegistration(registration).stream()
      .map(payment -> {
        Map<String, Object> booking = new HashMap<>();
        booking.put("id", payment.getRegistrationPaymentId());
        booking.put("type", payment.getType());
        booking.put("amount", payment.getAmount());
        // ISO with offset (server time zone), the browser shows it in local time
        booking.put("date", payment.getTransactionDate() == null ? null : payment.getTransactionDate().atZone(ZoneId.systemDefault()).toOffsetDateTime().toString());
        booking.put("comment", payment.getComment() == null ? null : outTextService.getOutTextMapByKey(payment.getComment()).get(0));
        return booking;
      })
      .toList();
  }

  // removes a payment, refund or fee of the registration, incl. its comment
  public void deleteBooking(Registration registration, Long registrationPaymentId) {
    RegistrationPayment payment = registrationPaymentRepo.findById(registrationPaymentId)
      .filter(booking -> booking.getRegistration().getRegistrationId().equals(registration.getRegistrationId()))
      .orElseThrow();
    if (payment.getComment() != null) {
      outTextService.delete(payment.getComment());
    }
    registrationPaymentRepo.delete(payment);
  }

  // changes a payment, refund or fee of the registration: the comment label is updated, created or removed
  public void updateBooking(Registration registration, Long registrationPaymentId, PaymentType type, Long amount, JsonObject comment) {
    RegistrationPayment payment = registrationPaymentRepo.findById(registrationPaymentId)
      .filter(booking -> booking.getRegistration().getRegistrationId().equals(registration.getRegistrationId()))
      .orElseThrow();
    JsonObject label = commentLabelText(comment);
    if (payment.getComment() != null && label != null) {
      outTextService.updateLabel(label, payment.getComment());
    } else if (payment.getComment() != null) {
      outTextService.delete(payment.getComment());
      payment.setComment(null);
    } else if (label != null) {
      payment.setComment(outTextService.newLabel(label));
    }
    payment.setType(type);
    payment.setAmount(amount);
    registrationPaymentRepo.save(payment);
  }

  // label only if a text is entered
  private String commentLabel(JsonObject comment) {
    JsonObject label = commentLabelText(comment);
    return label == null ? null : outTextService.newLabel(label);
  }

  // {EN, GE} or null without text, a missing language gets the text of the other one
  private static JsonObject commentLabelText(JsonObject comment) {
    String en = text(comment, "EN");
    String ge = text(comment, "GE");
    if (en.isBlank() && ge.isBlank()) {
      return null;
    }
    JsonObject label = new JsonObject();
    label.addProperty("EN", en.isBlank() ? ge : en);
    label.addProperty("GE", ge.isBlank() ? en : ge);
    return label;
  }

  private static String text(JsonObject label, String langKey) {
    return label != null && label.has(langKey) && !label.get(langKey).isJsonNull()
      ? label.get(langKey).getAsString().trim()
      : "";
  }

  public void onPaymentReceived(Registration registration, Long amount) throws IOException, TemplateException {

      // Update payment stats
      updatePayment(registration, amount);
      onPaymentUpdated(registration);
  }

  // fully paid: done and booking confirmation, else pending
  public void onPaymentUpdated(Registration registration) throws IOException, TemplateException {

      // Update workflow
     if (amountDue(registration) == 0) {
       updateRegistrationPaymentStatus(registration, PaymentStatus.PAID);

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
     else {
       updateRegistrationPaymentStatus(registration, PaymentStatus.PENDING);
     }
  }

  public void onPaymentConfirmed(Registration registration, Long amount, String status) throws IOException, TemplateException {
    switch (status.toLowerCase()) {
      case "confirmed":
        onPaymentReceived(registration, amount);
        break;

      case "expired":
      case "declined":
        updateRegistrationPaymentStatus(registration, PaymentStatus.DECLINED);
        break;

      case "cancelled":
      case "failed":
        updateRegistrationPaymentStatus(registration, PaymentStatus.FAILED);
        break;

      default:
        updateRegistrationPaymentStatus(registration, PaymentStatus.UNKNOWN);
        break;
    }
  }
}
