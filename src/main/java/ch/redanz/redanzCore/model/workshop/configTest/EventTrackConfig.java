package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.entities.EventTrack;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventTrackConfig {
  REDANZ_ADVANCED(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED),
  REDANZ_INTEREMDIATEE(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE),
  REDANZ_NO_LEVEL(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL)
  ;

  private final EventConfig eventConfig;
  private final TrackConfig trackConfig;

  public static void setup(EventService eventService, TrackService trackService) {
    for (EventTrackConfig eventTrackConfig : EventTrackConfig.values()) {
      Event event = eventService.findByName(eventTrackConfig.getEventConfig().getName());
      Track track = trackService.findByName(eventTrackConfig.getTrackConfig().getName());
      if (!eventService.eventTrackExists(eventService.findByName(eventTrackConfig.getEventConfig().getName()), trackService.findByName(eventTrackConfig.getTrackConfig().getName()))) {
        eventService.save(
          new EventTrack(
            track,
            event
          )
        );
      }
    }
  }
}
