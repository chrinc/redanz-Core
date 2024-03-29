package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlotId;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventTypeSlotRepo extends JpaRepository<EventTypeSlot, EventTypeSlotId> {
  List<EventTypeSlot> findAllByEvent(Event event);
  Optional<EventTypeSlot> findAllByEventAndTypeSlot(Event event, TypeSlot typeSlot);
  boolean existsByEventAndTypeSlot(Event event, TypeSlot typeSlot);

  @Override
  @Modifying
  @Query("delete from EventTypeSlot t where t.eventTypeSlotId = ?1")
  void deleteById(EventTypeSlotId eventTypeSlotId);

}
