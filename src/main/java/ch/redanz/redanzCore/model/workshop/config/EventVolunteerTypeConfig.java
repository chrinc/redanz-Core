package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public enum EventVolunteerTypeConfig {
  EVENT_TWO_HOURS(EventConfig.EVENT2023, VolunteerTypeConfig.TWO_HOURS),
  EVENT_SIX_HOURS(EventConfig.EVENT2023, VolunteerTypeConfig.SIX_HOURS),
  EVENT_NO_PREF_HOURS(EventConfig.EVENT2023, VolunteerTypeConfig.NO_PREFS);

  private final EventConfig eventConfig;
  private final VolunteerTypeConfig volunteerTypeConfig;


  EventVolunteerTypeConfig(EventConfig eventConfig, VolunteerTypeConfig volunteerTypeConfig) {
    this.eventConfig = eventConfig;
    this.volunteerTypeConfig = volunteerTypeConfig;
  }

  public static void setup(EventService eventService, VolunteerService volunteerService) {
    Map<Long, List<VolunteerType>> eventVolunteerTypeMap = new HashMap<>();

    for (EventVolunteerTypeConfig eventVolunteerTypeConfig : EventVolunteerTypeConfig.values()) {
      Event myEvent = eventService.findByName(eventVolunteerTypeConfig.getEventConfig().getName());
      VolunteerType volunteerType = volunteerService.findTypeByName(eventVolunteerTypeConfig.volunteerTypeConfig.getName());
      if (eventVolunteerTypeMap.containsKey(myEvent.getEventId())) {
        eventVolunteerTypeMap.get(myEvent.getEventId()).add(volunteerType);
      } else {
        List<VolunteerType> myTypes = new ArrayList<>();
        myTypes.add(volunteerType);
        eventVolunteerTypeMap.put(myEvent.getEventId(), myTypes);
      }
    }
    eventVolunteerTypeMap.keySet().forEach(eventId -> {
      Event myEvent = eventService.findByEventId(eventId);
      myEvent.setVolunteerTypes(eventVolunteerTypeMap.get(eventId));
      eventService.save(myEvent);
    });
  }
}
