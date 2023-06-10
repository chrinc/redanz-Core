package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlotId;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventTypeSlotRepo extends JpaRepository<EventTypeSlot, EventTypeSlotId> {
  List<EventTypeSlot> findAllByEvent(Event event);
  Optional<EventTypeSlot> findAllByEventAndTypeSlot(Event event, TypeSlot typeSlot);

}
