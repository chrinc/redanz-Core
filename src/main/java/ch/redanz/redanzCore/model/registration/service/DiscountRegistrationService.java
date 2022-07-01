package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountRegistrationService {
  private final DiscountRegistrationRepo discountRegistrationRepo;
  private final DiscountService discountService;

  public void saveDiscountRegistration(Registration registration, JsonArray discountRegistration) {
    discountRegistration.forEach(discount -> {
      discountRegistrationRepo.save(
        new DiscountRegistration(
          registration,
          discountService.findByDiscountId(discount.getAsJsonObject().get("discountId").getAsLong())
        )
      );
    });
  }
  public List<DiscountRegistration> findAllByRegistration(Registration registration) {
    return discountRegistrationRepo.findAllByRegistration(registration);
  }
}
