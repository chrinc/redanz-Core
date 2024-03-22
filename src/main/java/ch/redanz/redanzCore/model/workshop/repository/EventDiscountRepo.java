package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventDiscountRepo extends JpaRepository<EventDiscount, EventDiscountId> {
  List<EventDiscount> findAllByEvent(Event event);
  boolean existsByEventAndDiscount(Event event, Discount discount);
  EventDiscount findByEventAndDiscount(Event event, Discount discount);

  @Override
  @Modifying
  @Query("delete from EventDiscount t where t.eventDiscountId = ?1")
  void deleteById(EventDiscountId eventDiscountId);

}
