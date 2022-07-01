package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;

  public void save(Event event) {
    eventRepo.save(event);
  }
  public void save(EventBundle eventBundle) {
    bundleEventRepo.save(eventBundle);
  }

  public List<Event> getAllEvents() {
    return eventRepo.findAll();
  }

  public Event getCurrentEvent() {
    return eventRepo.findByName(EventConfig.EVENT2022.getName());
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
