package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  public void save(Discount discount) {
    discountRepo.save(discount);
  }
  public Discount findByDiscountId(Long discountId) {
    return discountRepo.findDiscountByDiscountId(discountId);
  }

  public Discount findByName(String name) {
    return discountRepo.findDiscountByName(name);
  }

  public List<Discount> findAll() {
    return discountRepo.findAll();
  }

}
