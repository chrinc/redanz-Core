package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventTypeSlotService {
  private final EventTypeSlotRepo eventTypeSlotRepo;
  private final SlotService slotService;

  public void updateEventTypeSlot(TypeSlot typeSlot, Event event, Integer seqNr) {
    if (!eventTypeSlotRepo.existsByEventAndTypeSlot(event, typeSlot)) {
      eventTypeSlotRepo.save(
        new EventTypeSlot(
          typeSlot,
          event,
          seqNr
        )
      );
    }
  }
  public EventTypeSlot findByEventAndTypeSlot(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.findAllByEventAndTypeSlot(event, typeSlot).get();
  }

  public List<EventTypeSlot> findByEventAndType(Event event, String type) {
    return eventTypeSlotRepo.findAllByEvent(event)
      .stream()
      .filter(slot -> slot.getTypeSlot().getType().equals(type))
      .collect(Collectors.toList());
  }

  public void deleteByEventAndType(Event event, String type) {
    findByEventAndType(event, type).forEach(this::delete);
  }


  @Transactional
  public void delete(EventTypeSlot eventTypeSlot) {
    eventTypeSlotRepo.deleteById(eventTypeSlot.getEventTypeSlotId());
  }
}
