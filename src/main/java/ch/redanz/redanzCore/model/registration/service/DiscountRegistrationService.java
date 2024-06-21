package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
@Slf4j
public class DiscountRegistrationService {
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final DiscountService discountService;
  private final WorkflowStatusService workflowStatusService;
  private final TrackService trackService;
  private final OutTextService outTextService;
  private final EventDiscountRepo eventDiscountRepo;

  public void save(Registration registration, Discount discount) {
    discountRegistrationRepo.save(
      new DiscountRegistration(
        registration,
        discount
      )
    );
  }

//  public List<DiscountRegistration> discountRegistrationsCapacity(Registration registration) {
//    List<DiscountRegistration> discountRegistrations = new ArrayList<>();
//    discountRegistrationRepo.findAllByRegistration(registration).forEach(
//      discountRegistration -> {
//        EventDiscount eventDiscount = eventDiscountRepo.findByEventAndDiscount(
//          registration.getEvent(), discountRegistration.getDiscount()
//        );
//        if (eventDiscountRepo.existsByEventDiscountIdAndCapacityNotNull(eventDiscount.getEventDiscountId()))  {
//          discountRegistrations.add(discountRegistration);
//        }
//      });
//    return discountRegistrations;
//  }


  public void saveCapacityDiscount(Registration registration) {
//    log.info(eventDiscount.getDiscount().getName());
//    log.info(registration.getBundle().getName());
//    log.info(String.valueOf(trackService.bundleHasTrack(registration.getBundle())));
    if (trackService.bundleHasTrack(registration.getBundle())) {
      registration.getTrack().getEventDiscounts().forEach(eventDiscount -> {
        if (
          eventDiscountRepo.existsByEventDiscountIdAndCapacityNotNull(eventDiscount.getEventDiscountId())
          && !discountRegistrationRepo.existsByRegistrationAndDiscount(registration, eventDiscount.getDiscount())
          && countDiscountSubmittedConfirmingDone(eventDiscount.getDiscount(), registration.getEvent()) < eventDiscount.getCapacity()
        ) {
          discountRegistrationRepo.save(
            new DiscountRegistration(
              registration,
              eventDiscount.getDiscount()
            )
          );
        }
      });
    }
  }

  public List<DiscountRegistration> discountRegistrationsNoCapacity(Registration registration) {
    List<DiscountRegistration> discountRegistrations = new ArrayList<>();
    discountRegistrationRepo.findAllByRegistration(registration).forEach(
      discountRegistration -> {

        EventDiscount eventDiscount = eventDiscountRepo.findByEventAndDiscount(
          registration.getEvent(), discountRegistration.getDiscount()
        );
      if (eventDiscountRepo.existsByEventDiscountIdAndCapacityIsNull(eventDiscount.getEventDiscountId()))  {
        discountRegistrations.add(discountRegistration);
      }
    });

    return discountRegistrations;
  }

  public List<DiscountRegistration> discountRegistrations(Registration registration, JsonObject discountRegistrationRequest) {
    List<DiscountRegistration> discountRegistrations = new ArrayList<>();
    if (discountRegistrationRequest.get("discountRegistration") != null
      && !discountRegistrationRequest.get("discountRegistration").getAsJsonArray().isEmpty()) {
      JsonArray discountRequests = discountRegistrationRequest
        .get("discountRegistration")
        .getAsJsonArray();

      discountRequests.forEach(discount -> {
        Discount newDiscount = null;
        if (discount.getAsJsonObject().get("checked") == null) {
          newDiscount = discountService.findByDiscountId(discount.getAsJsonObject().get("discountId").getAsLong());
        } else if (discount.getAsJsonObject().get("checked").getAsBoolean()) {
          newDiscount = discountService.findByDiscountId(discount.getAsJsonObject().get("discount").getAsJsonObject().get("discountId").getAsLong());
        }
        if (
          newDiscount != null
            && eventDiscountRepo.existsByEventDiscountIdAndCapacityIsNull(
            eventDiscountRepo.findByEventAndDiscount(registration.getEvent(), newDiscount).getEventDiscountId()
          )
        ) {
          discountRegistrations.add(new DiscountRegistration(
            registration,
            newDiscount
          ));
        }
      });
    }
    return discountRegistrations;
  }

  private boolean hasDiscountRegistration(List<DiscountRegistration> discountRegistrations, Discount discount) {
    AtomicBoolean hasDiscountRegistration = new AtomicBoolean(false);
    discountRegistrations.forEach(discountRegistration -> {
      if (discountRegistration.getDiscount() == discount) {
        hasDiscountRegistration.set(true);
      }
    });

    return hasDiscountRegistration.get();
  }

  public void updateDiscountRegistrationRequest(Registration registration, JsonObject request) {
    List<DiscountRegistration> requestDiscountRegistrations = discountRegistrations(registration, request);
//    log.info("requestDiscountRegistrations, size {}", requestDiscountRegistrations.size());
    List<DiscountRegistration> discountRegistrationsNoCapacity = discountRegistrationsNoCapacity(registration);
//    log.info("discountRegistrationsNoCapacity, size {}", discountRegistrationsNoCapacity.size());


    // delete in current if not in request
    discountRegistrationsNoCapacity.forEach(
      discountRegistration -> {
      if (!hasDiscountRegistration(requestDiscountRegistrations, discountRegistration.getDiscount())){
        discountRegistrationRepo.deleteAllByRegistrationAndDiscount(registration, discountRegistration.getDiscount());
      }
    });

    // add new from request
    requestDiscountRegistrations.forEach(
      discountRegistration -> {
      if (!hasDiscountRegistration(discountRegistrationsNoCapacity, discountRegistration.getDiscount())){
        save(registration, discountRegistration.getDiscount());
      }
    });
  }

  public void saveDiscountRegistration(Registration registration, JsonArray discountRegistration) {
    discountRegistration.forEach(discount -> {
      save(
        registration,
        discountService.findByDiscountId(discount.getAsJsonObject().get("discountId").getAsLong())
      );
    });
  }

  public List<DiscountRegistration> findAllByRegistration(Registration registration) {
    return discountRegistrationRepo.findAllByRegistration(registration);
  }

  public int countDiscountSubmitted(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getSubmitted(), event
    );
  }
  public int countDiscountConfirming(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getConfirming(), event
    );
  }
  public int countDiscountDone(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getDone(), event
    );
  }
  public int countDiscountSubmittedConfirmingAndDone(Discount discount, Event event) {
    return countDiscountConfirming(discount,event) + countDiscountDone(discount, event) + countDiscountSubmitted(discount, event);
  }
  public int countDiscountConfirmingAndDone(Discount discount, Event event) {
    return countDiscountConfirming(discount, event) + countDiscountDone(discount, event);
  }


  public int countDiscountSubmittedConfirmingDone(Discount discount, Event event) {
    return countDiscountConfirmingAndDone(discount, event)
      + countDiscountDone(discount, event)
      ;
  }

  public List<String> countDiscountSubmittedAsList(Discount discount, Event event){
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countDiscountSubmitted(discount, event)));
    return discountList;

  }
  public List<String>  countDiscountConfirmingAsList(Discount discount, Event event){
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countDiscountConfirming(discount, event)));
    return discountList;
  }
  public List<String> countDiscountDoneAsList(Discount discount, Event event){
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countDiscountDone(discount, event)));
    return discountList;
  }
  public List<String> countDiscountSubmittedConfirmingAndDoneAsList(Discount discount, Event event) {
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countDiscountSubmittedConfirmingAndDone(discount, event)));
    return discountList;
  }

  public String getReportDiscounts(Registration registration, Language language) {
    AtomicReference<String> discounts = new AtomicReference<>();
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      String specialOutText = outTextService.getOutTextByKeyAndLangKey(discountRegistration.getDiscount().getName(), language.getLanguageKey()).getOutText();
      if (discounts.get() == null)
        discounts.set(specialOutText);
      else {
        discounts.set(discounts.get() + ", " + specialOutText);
      }
    });
    return discounts.get() == null ? "" : discounts.toString();
  }

}
