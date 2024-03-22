package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.repository.service.BundleService;
import ch.redanz.redanzCore.model.workshop.repository.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventBundleConfig {
  EVENT2023_FULL_PASS(EventConfig.REDANZ_EVENT, BundleConfig.FULL_PASS),
  EVENT2023_HALF_PASS(EventConfig.REDANZ_EVENT, BundleConfig.HALF_PASS),
  EVENT2023_PARTY(EventConfig.REDANZ_EVENT, BundleConfig.PARTYPASS),
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
