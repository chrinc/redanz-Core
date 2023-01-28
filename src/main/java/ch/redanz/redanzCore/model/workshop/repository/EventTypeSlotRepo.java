package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlotId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTypeSlotRepo extends JpaRepository<EventTypeSlot, EventTypeSlotId> {
  List<EventTypeSlot> findAllByEvent(Event event);

}
