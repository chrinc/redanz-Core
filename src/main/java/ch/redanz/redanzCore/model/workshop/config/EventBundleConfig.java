package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventBundleConfig {
  EVENT2022_FULL(EventConfig.EVENT2022, BundleConfig.FULLPASS),
  EVENT2022_HALF(EventConfig.EVENT2022, BundleConfig.HALFPASS),
  EVENT2022_LEVEL(EventConfig.EVENT2022, BundleConfig.LEVELPASS),
  EVENT2022_PARTY(EventConfig.EVENT2022, BundleConfig.PARTYPASS);

  private final EventConfig event;
  private final BundleConfig bundle;

  EventBundleConfig(EventConfig event, BundleConfig bundle) {
    this.event = event;
    this.bundle = bundle;
  }

  public static void setup(BundleService bundleService, EventService eventService) {

    for (EventBundleConfig eventBundleConfig : EventBundleConfig.values()) {
      eventService.save(
        new EventBundle(
          bundleService.findByName(eventBundleConfig.getBundle().getName()),
          eventService.findByName(eventBundleConfig.getEvent().getName())
        )
      );
    }
  }
}
