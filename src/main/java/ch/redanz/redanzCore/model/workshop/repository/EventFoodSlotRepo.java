package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFoodSlotRepo  extends JpaRepository<EventFoodSlot, Long> {
  EventFoodSlot findByEventAndFoodAndSlot(Event event, Food food, Slot slot);
  List<EventFoodSlot> findByEvent(Event event);
  EventFoodSlot findByEventFoodSlotId(Long eventFoodSlotId);
}
