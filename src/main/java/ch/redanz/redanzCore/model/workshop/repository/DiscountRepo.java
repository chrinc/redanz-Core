package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepo extends JpaRepository<Discount, Long> {
  Discount findByName(String name);
  Discount findDiscountByDiscountId(Long discountId);
  Discount findDiscountByName(String name);
}
