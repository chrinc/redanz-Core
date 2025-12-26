package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFoodSlotRepo extends JpaRepository<EventFoodSlot, Long> {
  EventFoodSlot findByEventAndFoodAndSlot(Event event, Food food, Slot slot);
  Boolean existsByFood(Food food);
  List<EventFoodSlot> findByEvent(Event event);

  EventFoodSlot findByEventFoodSlotId(Long eventFoodSlotId);

}
