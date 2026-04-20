package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDiscountRepo extends JpaRepository<EventDiscount, Long> {
  List<EventDiscount> findAllByEvent(Event event);
  EventDiscount findByEventDiscountId(Long eventDiscountId);
  boolean existsByEventDiscountIdAndCapacityIsNull(Long eventDiscountId);
  boolean existsByEventDiscountIdAndCapacityNotNull(Long eventDiscountId);
  boolean existsByName(String name);
  EventDiscount findByName(String name);
}
