package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;
  private final EventTypeSlotRepo eventTypeSlotRepo;

  public void save(Event event) {
    eventRepo.save(event);
  }
  public void save(EventBundle eventBundle) {
    bundleEventRepo.save(eventBundle);
  }
  public void save (EventTypeSlot eventTypeSlot) {
    this.eventTypeSlotRepo.save(eventTypeSlot);
  }

  public List<Event> getAllEvents() {
    return eventRepo.findAllByArchived(false);
  }

  public Event getCurrentEvent() {
    return eventRepo.findByName(EventConfig.EVENT2022.getName());
  }

  public Event getById(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }

  public List<Event> getActiveEvents() {
    return eventRepo.findAllByActiveAndArchived(true, false);
  }
  public List<Event> getInactiveEvents() {
    return eventRepo.findAllByActiveAndArchived(false, false);
  }

  public Event findByEventId(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }

  public Event findByName(String name) {
    return eventRepo.findByName(name);
  }

  public List<Event> findAll() {
    return eventRepo.findAll();
  }
}
