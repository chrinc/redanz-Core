package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventBundleConfig {
  EVENT_FULL(EventConfig.REDANZ_EVENT, BundleConfig.FULL_PASS),
  EVENT_HALF(EventConfig.REDANZ_EVENT, BundleConfig.HALF_PASS),
  EVENT_PARTY(EventConfig.REDANZ_EVENT, BundleConfig.PARTYPASS);
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
