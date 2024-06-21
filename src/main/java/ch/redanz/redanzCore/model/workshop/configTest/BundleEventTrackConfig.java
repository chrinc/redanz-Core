package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleEventTrackConfig {
  FULL_ADVANCED(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, BundleConfig.FULL_PASS),
  FULL_INTEREMDIATE(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, BundleConfig.FULL_PASS),
  FULL_NO_LEVEL(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, BundleConfig.FULL_PASS),

  HALF_ADVANCED(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, BundleConfig.HALF_PASS),
  HALF_INTEREMDIATE(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, BundleConfig.HALF_PASS),
  HALF_NO_LEVEL(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, BundleConfig.HALF_PASS);

  private final EventConfig eventConfig;
  private final TrackConfig trackConfig;
  private final BundleConfig bundleConfig;

  public static void setup(EventService eventService, TrackService trackService, BundleService bundleService) {
    for (BundleEventTrackConfig bundleEventTrackConfig : BundleEventTrackConfig.values()) {
      Event event = eventService.findByName(bundleEventTrackConfig.getEventConfig().getName());
      Bundle bundle = bundleService.findByName(bundleEventTrackConfig.getBundleConfig().getName());
      Track track = trackService.findByName(bundleEventTrackConfig.getTrackConfig().getName());
      EventTrack eventTrack = eventService.findByEventAndTrack(event, track);
      bundle.getEventTracks().add(eventTrack);
      bundleService.save(bundle);
    }
  }
}
