package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventDiscountRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DiscountService {
  private final DiscountRepo discountRepo;
  private final EventDiscountRepo eventDiscountRepo;
  public void save(Discount discount) {
    discountRepo.save(discount);
  }
  public void save(EventDiscount eventDiscount) {
    eventDiscountRepo.save(eventDiscount);
  }
  public Discount findByDiscountId(Long discountId) {
    return discountRepo.findDiscountByDiscountId(discountId);
  }
  public boolean existsByName(String name) {
    return discountRepo.existsByName(name);
  }
  public boolean eventDiscountExists(Event event, Discount discount) {
    return eventDiscountRepo.existsByEventAndDiscount(event, discount);
  }
  public Discount findByName(String name) {
    return discountRepo.findDiscountByName(name);
  }

  public List<Discount> findAll() {
    return discountRepo.findAll();
  }
  public List<Discount> findAllByEvent(Event event) {
    List<Discount> discounts = new ArrayList<>();
    eventDiscountRepo.findAllByEvent(event).forEach(eventDiscount -> {
      discounts.add(eventDiscount.getDiscount());
    });
    return discounts;
  }


}
