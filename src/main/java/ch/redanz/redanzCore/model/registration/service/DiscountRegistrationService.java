package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.config.BundleConfig;
import ch.redanz.redanzCore.model.workshop.config.DiscountConfig;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
@Slf4j
public class DiscountRegistrationService {
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final DiscountService discountService;
  private final WorkflowStatusService workflowStatusService;
  private final TrackService trackService;
  private final EventService eventService;
  private final BundleService bundleService;

  public void save(Registration registration, Discount discount) {
    discountRegistrationRepo.save(
      new DiscountRegistration(
        registration,
        discount
      )
    );
  }
  public void saveEarlyBird(Registration registration, int currentRegistrationCount) {
    log.info("inc@save early bird, bundleHasTrack: " + trackService.bundleHasTrack(registration.getBundle()));
    if (trackService.bundleHasTrack(registration.getBundle())
      && currentRegistrationCount < DiscountConfig.EARLY_BIRD.getCapacity()
      && !discountRegistrationRepo.findAllByRegistrationAndDiscount(registration, discountService.findByName(DiscountConfig.EARLY_BIRD.getName())).isPresent()
    )
    discountRegistrationRepo.save(
      new DiscountRegistration(
        registration,
        discountService.findByName(DiscountConfig.EARLY_BIRD.getName())
      )
    );
  }

  public List<DiscountRegistration> discountRegistrationsExceptEarlyBird(Registration registration) {
    List<DiscountRegistration> discountRegistrations =  discountRegistrationRepo.findAllByRegistration(registration);
    if (discountRegistrationRepo.findAllByRegistrationAndDiscount(registration, discountService.findByName(DiscountConfig.EARLY_BIRD.getName())).isPresent()) {
      discountRegistrations.remove(discountRegistrations.indexOf(
          discountRegistrationRepo
            .findAllByRegistrationAndDiscount(
              registration,
              discountService.findByName(DiscountConfig.EARLY_BIRD.getName())
            ).get()
        )
      );
    }
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
        if (discount.getAsJsonObject().get("checked") == null) {
          discountRegistrations.add(
            new DiscountRegistration(
              registration,
              discountService.findByDiscountId(discount.getAsJsonObject().get("discountId").getAsLong())
            )
          );
        } else
          if (discount.getAsJsonObject().get("checked").getAsBoolean()) {
            discountRegistrations.add(
              new DiscountRegistration(
                registration,
                discountService.findByDiscountId(discount.getAsJsonObject().get("discount").getAsJsonObject().get("discountId").getAsLong())
              )
            );
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
    List<DiscountRegistration> discountRegistrations = discountRegistrationsExceptEarlyBird(registration);

    // delete in current if not in request
    discountRegistrations.forEach(discountRegistration -> {
      if (!hasDiscountRegistration(requestDiscountRegistrations, discountRegistration.getDiscount())){
        discountRegistrationRepo.deleteAllByRegistrationAndDiscount(registration, discountRegistration.getDiscount());
      }
    });

    // add new from request
    requestDiscountRegistrations.forEach(discountRegistration -> {
      if (!hasDiscountRegistration(discountRegistrations, discountRegistration.getDiscount())){
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

  public int countDiscountSubmittedByEvent(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getSubmitted(), event
    );
  }
  public int countDiscountConfirmingByEvent(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getConfirming(), event
    );
  }
  public int countDiscountDoneByEvent(Discount discount, Event event){
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getDone(), event
    );
  }
  public int countDiscountSubmittedConfirmingAndDoneByEvent(Discount discount, Event event) {
    return countDiscountConfirmingByEvent(discount,event) + countDiscountDoneByEvent(discount, event) + countDiscountSubmittedByEvent(discount, event);
  }
  public int countDiscountConfirmingAndDoneByEvent(Discount discount, Event event) {
    return countDiscountConfirmingByEvent(discount, event) + countDiscountDoneByEvent(discount, event);
  }
}
