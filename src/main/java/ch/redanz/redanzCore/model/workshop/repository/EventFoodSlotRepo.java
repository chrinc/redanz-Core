package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventFoodSlotRepo extends JpaRepository<EventFoodSlot, Long> {
  List<EventFoodSlot> findByEvent(Event event);

  EventFoodSlot findByEventFoodSlotId(Long eventFoodSlotId);

  EventFoodSlot findByName(String name);
  Boolean existsByEventSlot(EventSlot eventSlot);
}
