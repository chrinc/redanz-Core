package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Discount;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  public Discount findByDiscountId(Long discountId){
    return discountRepo.findDiscountByDiscountId(discountId);
  }
  public Discount findByName(String name){
    return discountRepo.findDiscountByName(name);
  }
}
