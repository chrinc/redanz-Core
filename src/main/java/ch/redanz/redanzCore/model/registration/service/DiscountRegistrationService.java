package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.config.BundleConfig;
import ch.redanz.redanzCore.model.workshop.config.DiscountConfig;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountRegistrationService {
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final DiscountService discountService;
  private final WorkflowStatusService workflowStatusService;
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
    if (registration.getBundle() != bundleService.findByName(BundleConfig.PARTYPASS.getName())
      && currentRegistrationCount < DiscountConfig.EARLY_BIRD.getCapacity()
    )
    discountRegistrationRepo.save(
      new DiscountRegistration(
        registration,
        discountService.findByName(DiscountConfig.EARLY_BIRD.getName())
      )
    );
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


  public int countDiscountReleasedAndDone(Discount discount) {
    return discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
      discount, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
    )
      +
      discountRegistrationRepo.countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(
        discount, workflowStatusService.getDone(), eventService.getCurrentEvent()
      );
  }
}
