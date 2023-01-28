package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventDiscountRepo extends JpaRepository<EventDiscount, EventDiscountId> {
  List<EventDiscount> findAllByEvent(Event event);
}
