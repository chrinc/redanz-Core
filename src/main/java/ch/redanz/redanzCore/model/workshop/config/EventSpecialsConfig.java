package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.repository.service.EventService;
import ch.redanz.redanzCore.model.workshop.repository.service.SpecialService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public enum EventSpecialsConfig {
  REDANZ_EVENT_SPECIAL(EventConfig.REDANZ_EVENT, SpecialConfig.FULL_PASS_SPECIAL)
  ;

  private final EventConfig eventConfig;
  private final SpecialConfig specialConfig;

  EventSpecialsConfig(EventConfig eventConfig, SpecialConfig specialConfig) {
    this.eventConfig = eventConfig;
    this.specialConfig = specialConfig;
  }

  public static void setup(SpecialService specialService, EventService eventService) {
    Map<Long, List<Special>> eventSpcialsMap = new HashMap<>();
    for (EventSpecialsConfig eventSpecialsConfig : EventSpecialsConfig.values()) {
      Event myEvent = eventService.findByName(eventSpecialsConfig.eventConfig.getName());
      Special special = specialService.findByName(eventSpecialsConfig.specialConfig.getName());
      if (eventSpcialsMap.containsKey(myEvent.getEventId())) {
        eventSpcialsMap.get(myEvent.getEventId()).add(special);
      } else {
        List<Special> mySpecials = new ArrayList<>();
        mySpecials.add(special);
        eventSpcialsMap.put(myEvent.getEventId(), mySpecials);
      }
    }
    eventSpcialsMap.keySet().forEach(eventId -> {
      Event myEvent = eventService.findByEventId(eventId);
      myEvent.setSpecials(eventSpcialsMap.get(eventId));
      eventService.save(myEvent);
    });
  }
}
