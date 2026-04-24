package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.config.SlotType;
import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Map;

public interface EventSlotRepo extends JpaRepository<EventSlot, Long> {
  List<EventSlot> findAllByEvent(Event event);
  Boolean existsByName(String name);
  EventSlot findByName(String name);
  EventSlot findByEventSlotId(Long eventSlotId);
  EventSlot findFirstByNameAndSlotTypeAndEvent(String name, SlotType slotType, Event event);
  List<EventSlot> findAllByEventAndSlotType(Event event, SlotType slotType);
}
