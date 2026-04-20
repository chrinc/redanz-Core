package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
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

  public void save(Registration registration, EventDiscount eventDiscount) {
    discountRegistrationRepo.save(
      new DiscountRegistration(
        registration,
        eventDiscount
      )
    );
  }

  public Boolean hasRegistrations(EventDiscount eventDiscount, Boolean active) {
    return discountRegistrationRepo.existsByEventDiscountAndRegistrationActive(eventDiscount, active);
  }


  public void saveCapacityDiscount(Registration registration) {
    if (trackService.bundleHasTrack(registration.getBundle())) {
      registration.getTrack().getEventDiscounts().forEach(eventDiscount -> {
        if (
          eventDiscountRepo.existsByEventDiscountIdAndCapacityNotNull(eventDiscount.getEventDiscountId())
          && !discountRegistrationRepo.existsByRegistrationAndEventDiscount(registration, eventDiscount)
          && countEventDiscountSubmittedConfirmingDone(eventDiscount) < eventDiscount.getCapacity()
        ) {
          discountRegistrationRepo.save(
            new DiscountRegistration(
              registration,
              eventDiscount
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
      if (eventDiscountRepo.existsByEventDiscountIdAndCapacityIsNull(discountRegistration.getEventDiscount().getEventDiscountId()))  {
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
        EventDiscount newDiscount = null;
        if (discount.getAsJsonObject().get("checked") == null ||discount.getAsJsonObject().get("checked").getAsBoolean()) {
          newDiscount = discountService.findByEventDiscountId(discount.getAsJsonObject().get("eventDiscountId").getAsLong());
        }

        if (
          newDiscount != null
            && newDiscount.getCapacity() == null
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

  private boolean hasDiscountRegistration(List<DiscountRegistration> discountRegistrations, EventDiscount eventDiscount) {
    return discountRegistrations.stream()
      .anyMatch(discountRegistration ->
        java.util.Objects.equals(discountRegistration.getEventDiscount(), eventDiscount));
  }

  public void updateDiscountRegistrationRequest(Registration registration, JsonObject request) {
    List<DiscountRegistration> requestDiscountRegistrations = discountRegistrations(registration, request);
    List<DiscountRegistration> discountRegistrationsNoCapacity = discountRegistrationsNoCapacity(registration);

    // delete in current if not in request
    discountRegistrationsNoCapacity.forEach(
      discountRegistration -> {
      if (!hasDiscountRegistration(requestDiscountRegistrations, discountRegistration.getEventDiscount())){
        discountRegistrationRepo.deleteAllByRegistrationAndEventDiscount(registration, discountRegistration.getEventDiscount());
      }
    });

    // add new from request
    requestDiscountRegistrations.forEach(
      discountRegistration -> {
      if (!hasDiscountRegistration(discountRegistrationsNoCapacity, discountRegistration.getEventDiscount())){
        save(registration, discountRegistration.getEventDiscount());
      }
    });
  }

  public void saveDiscountRegistration(Registration registration, JsonArray discountRegistration) {
    discountRegistration.forEach(discount -> {
      save(
        registration,
        discountService.findByEventDiscountId(discount.getAsJsonObject().get("eventDiscountId").getAsLong())
      );
    });
  }

  public List<DiscountRegistration> findAllByRegistration(Registration registration) {
    return discountRegistrationRepo.findAllByRegistration(registration);
  }

  public int countEventDiscountSubmitted(EventDiscount eventDiscount){
    return discountRegistrationRepo.countAllByEventDiscountAndRegistrationWorkflowStatus(
      eventDiscount, workflowStatusService.getSubmitted()
    );
  }
  public int countEventDiscountConfirming(EventDiscount eventDiscount){
    return discountRegistrationRepo.countAllByEventDiscountAndRegistrationWorkflowStatus(
      eventDiscount, workflowStatusService.getConfirming()
    );
  }
  public int countEventDiscountDone(EventDiscount eventDiscount){
    return discountRegistrationRepo.countAllByEventDiscountAndRegistrationWorkflowStatus(
      eventDiscount, workflowStatusService.getDone()
    );
  }
  public int countEventDiscountSubmittedConfirmingAndDone(EventDiscount eventDiscount) {
    return countEventDiscountConfirming(eventDiscount) + countEventDiscountDone(eventDiscount) + countEventDiscountSubmitted(eventDiscount);
  }
  public int countEventDiscountConfirmingAndDone(EventDiscount eventDiscount) {
    return countEventDiscountConfirming(eventDiscount) + countEventDiscountDone(eventDiscount);
  }


  public int countEventDiscountSubmittedConfirmingDone(EventDiscount eventDiscount) {
    return countEventDiscountSubmitted(eventDiscount)
      + countEventDiscountConfirming(eventDiscount)
      + countEventDiscountDone(eventDiscount)
      ;
  }

  public List<String> countEventDiscountSubmittedAsList(EventDiscount eventDiscount){
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countEventDiscountSubmitted(eventDiscount)));
    return discountList;

  }
  public List<String>  countEventDiscountConfirmingAsList(EventDiscount eventDiscount){
    List<String> discountList = new ArrayList<>();
    discountList.add(String.valueOf(countEventDiscountConfirming(eventDiscount)));
    return discountList;
  }
  public List<String> countEventDiscountDoneAsList(EventDiscount eventDiscount){
    List<String> eventDiscountList = new ArrayList<>();
    eventDiscountList.add(String.valueOf(countEventDiscountDone(eventDiscount)));
    return eventDiscountList;
  }
  public List<String> countEventDiscountSubmittedConfirmingAndDoneAsList(EventDiscount eventDiscount) {
    List<String> eventDiscountList = new ArrayList<>();
    eventDiscountList.add(String.valueOf(countEventDiscountSubmittedConfirmingAndDone(eventDiscount)));
    return eventDiscountList;
  }

  public String getReportDiscounts(Registration registration, Language language) {


    AtomicReference<String> discounts = new AtomicReference<>();
    discountRegistrationRepo.findAllByRegistration(registration).forEach(discountRegistration -> {
      String specialOutText = outTextService.getOutTextByKeyAndLangKey(discountRegistration.getEventDiscount().getName(), language.getLanguageKey()).getOutText();
      if (discounts.get() == null)
        discounts.set(specialOutText);
      else {
        discounts.set(discounts.get() + ", " + specialOutText);
      }
    });
    return discounts.get() == null ? "" : discounts.toString();
  }

}
