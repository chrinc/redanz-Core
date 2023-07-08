package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventPrivateClassConfig {
  EVENT2023_ELZE(EventConfig.EVENT2023, PrivateClassConfig.ELZE),
  EVENT2023_HECTOR_SONJA(EventConfig.EVENT2023, PrivateClassConfig.SONJA_HECTOR),
  EVENT2023_BARA(EventConfig.EVENT2023, PrivateClassConfig.BARA),
  EVENT2023_CLAUDIA(EventConfig.EVENT2023, PrivateClassConfig.CLAUDIA)
  ;

  private final EventConfig eventConfig;
  private final PrivateClassConfig privateClassConfig;

  public static void setup(PrivateClassService privateClassService, EventService eventService) {
    Map<Long, List<PrivateClass>> eventPrivateClassMap = new HashMap<>();
    for (EventPrivateClassConfig eventPrivateClassConfig : EventPrivateClassConfig.values()) {
      Event myEvent = eventService.findByName(eventPrivateClassConfig.eventConfig.getName());
      PrivateClass privateClass = privateClassService.findByName(eventPrivateClassConfig.privateClassConfig.getName());
      log.info("private Class" + privateClass.getName());
      if (eventPrivateClassMap.containsKey(myEvent.getEventId())) {
        eventPrivateClassMap.get(myEvent.getEventId()).add(privateClass);
      } else {
        List<PrivateClass> myPrivateclasses = new ArrayList<>();
        myPrivateclasses.add(privateClass);
        eventPrivateClassMap.put(myEvent.getEventId(), myPrivateclasses);
      }
    }
    log.info("eventPrivateClassMap.size(): " + eventPrivateClassMap.size());
    eventPrivateClassMap.keySet().forEach(eventId -> {
      Event myEvent = eventService.findByEventId(eventId);
      myEvent.setPrivateClasses(eventPrivateClassMap.get(eventId));
      eventService.save(myEvent);
    });
  }
}
