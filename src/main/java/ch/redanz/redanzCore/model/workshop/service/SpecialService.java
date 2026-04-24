package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventSpecialRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialService {
  private final OutTextService outTextService;
  private final EventRepo eventRepo;
  private final EventSpecialRepo eventSpecialRepo;


  public void save(EventSpecial eventSpecial) {
    eventSpecialRepo.save(eventSpecial);
  }

  public List<EventSpecial> findByEventInfoOnly(Event event, Boolean infoOnly) {
    return eventSpecialRepo.findAllByEventAndInfoOnly(event, infoOnly);
  }

  public List<EventSpecial> findAll() {
    return eventSpecialRepo.findAll();
  }

  public void delete (EventSpecial eventSpecial) {
    outTextService.delete(eventSpecial.getName());
    outTextService.delete(eventSpecial.getDescription());
    eventSpecialRepo.delete(eventSpecial);
  }

  public EventSpecial findFirstByNameAndEvent(String name, Event event) {
    return eventSpecialRepo.findFirstByNameAndEvent(name, event);

  }


  public boolean existsByName(String name) {
    return eventSpecialRepo.existsByName(name);
  }

  public EventSpecial findByName(String name) {
    return eventSpecialRepo.findByName(name);
  }
  public EventSpecial findByEventSpecialId(Long eventSpecialId) {
    return eventSpecialRepo.findByEventSpecialId(eventSpecialId);
  }
  public List<Map<String, String>> getEventSpecialSchema(){
    return EventSpecial.schema();
  }

  public List<Map<String, String>> getEventSpecialData() {
    List<Map<String, String>> eventSpecialsData = new ArrayList<>();
    eventSpecialRepo.findAll().forEach(eventSpecial -> {
      // special data
      Map<String, String> eventSpecialData = eventSpecial.dataMap();
      eventSpecialsData.add(eventSpecialData);
    });
    return eventSpecialsData;
  }
}
