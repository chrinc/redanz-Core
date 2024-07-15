package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleEventTrackConfig {
  FULL_ADVANCED(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, BundleConfig.FULL_PASS, 100, Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),
  FULL_INTEREMDIATE(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, BundleConfig.FULL_PASS, 100,Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),
  FULL_NO_LEVEL(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, BundleConfig.FULL_PASS,100, Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),

  HALF_ADVANCED(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, BundleConfig.HALF_PASS, 80,Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),
  HALF_INTEREMDIATE(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, BundleConfig.HALF_PASS, 80,Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),
  HALF_NO_LEVEL(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, BundleConfig.HALF_PASS, 80, Set.of(EventDanceRoleConfig.EVENT_FOLLOW, EventDanceRoleConfig.EVENT_SWITCH, EventDanceRoleConfig.EVENT_LEAD)),
  ;

  private final EventConfig eventConfig;
  private final TrackConfig trackConfig;
  private final BundleConfig bundleConfig;
  private final int capacity;
  private final Set<EventDanceRoleConfig> eventDanceRoleList;

  public static void setup(EventService eventService, TrackService trackService, BundleService bundleService,
  BundleEventTrackService bundleEventTrackService, DanceRoleService danceRoleService
  ) {
    for (BundleEventTrackConfig bundleEventTrackConfig : BundleEventTrackConfig.values()) {
      Event event = eventService.findByName(bundleEventTrackConfig.getEventConfig().getName());
      Bundle bundle = bundleService.findByName(bundleEventTrackConfig.getBundleConfig().getName());
      Track track = trackService.findByName(bundleEventTrackConfig.getTrackConfig().getName());
      EventTrack eventTrack = eventService.findByEventAndTrack(event, track);

      bundleService.save(bundle);
      BundleEventTrack bundleEventTrack = new BundleEventTrack(bundle, eventTrack, bundleEventTrackConfig.capacity);
      bundleEventTrackService.save(bundleEventTrack);
      bundleEventTrackConfig.eventDanceRoleList.forEach(eventDanceRoleConfig -> {
        bundleEventTrackService.save(
          new BundleEventTrackDanceRole(
            bundleEventTrack
            , eventService.findByEventAndDanceRole(event, danceRoleService.findByName(eventDanceRoleConfig.getDanceRoleConfig().getName()))
          )
        );
      });

    }
  }
}
