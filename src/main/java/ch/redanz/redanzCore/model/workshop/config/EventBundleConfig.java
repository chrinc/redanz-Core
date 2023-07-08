package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventBundleConfig {
  EVENT2023_EXTRA_FUN(EventConfig.EVENT2023, BundleConfig.EXTRA_FUN_PASS),
  EVENT2023_FUN(EventConfig.EVENT2023, BundleConfig.FUN_PASS),
  EVENT2023_PARTY(EventConfig.EVENT2023, BundleConfig.PARTYPASS),
  EVENT2023_FRI_SPECIAL(EventConfig.EVENT2023, BundleConfig.FRIDAY_SPECIAL),
  EVENT2023_SUN_PARTY(EventConfig.EVENT2023, BundleConfig.PARTY_SUN)
  ;

  private final EventConfig event;
  private final BundleConfig bundle;

  EventBundleConfig(EventConfig event, BundleConfig bundle) {
    this.event = event;
    this.bundle = bundle;
  }

  public static void setup(BundleService bundleService, EventService eventService) {

    for (EventBundleConfig eventBundleConfig : EventBundleConfig.values()) {
      if (!eventService.eventBundleExists(eventService.findByName(eventBundleConfig.getEvent().getName()), bundleService.findByName(eventBundleConfig.getBundle().getName()))) {
        eventService.save(
          new EventBundle(
            bundleService.findByName(eventBundleConfig.getBundle().getName()),
            eventService.findByName(eventBundleConfig.getEvent().getName())
          )
        );
      }
    }
  }
}
