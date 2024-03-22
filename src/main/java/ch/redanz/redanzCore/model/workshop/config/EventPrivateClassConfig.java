package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
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
  EVENT_ELZE(EventConfig.REDANZ_EVENT, PrivateClassConfig.ELZE),
  EVENT_HECTOR_SONJA(EventConfig.REDANZ_EVENT, PrivateClassConfig.SONJA_HECTOR),
  EVENT_BARA(EventConfig.REDANZ_EVENT, PrivateClassConfig.BARA),
  EVENT_CLAUDIA(EventConfig.REDANZ_EVENT, PrivateClassConfig.CLAUDIA)
  ;

  private final EventConfig eventConfig;
  private final PrivateClassConfig privateClassConfig;

  public static void setup(PrivateClassService privateClassService, EventService eventService) {
    Map<Long, List<PrivateClass>> eventPrivateClassMap = new HashMap<>();
    for (EventPrivateClassConfig eventPrivateClassConfig : EventPrivateClassConfig.values()) {
      Event myEvent = eventService.findByName(eventPrivateClassConfig.eventConfig.getName());
      PrivateClass privateClass = privateClassService.findByName(eventPrivateClassConfig.privateClassConfig.getName());
      if (eventPrivateClassMap.containsKey(myEvent.getEventId())) {
        eventPrivateClassMap.get(myEvent.getEventId()).add(privateClass);
      } else {
        List<PrivateClass> myPrivateclasses = new ArrayList<>();
        myPrivateclasses.add(privateClass);
        eventPrivateClassMap.put(myEvent.getEventId(), myPrivateclasses);
      }
    }
    eventPrivateClassMap.keySet().forEach(eventId -> {
      Event myEvent = eventService.findByEventId(eventId);
      myEvent.setPrivateClasses(eventPrivateClassMap.get(eventId));
      eventService.save(myEvent);
    });
  }
}
