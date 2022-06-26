package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.EventBundle;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
  public static List<EventBundle> setup(BundleRepo bundleRepo, EventRepo eventRepo) {
    List<EventBundle> transitions = new ArrayList<>();

    for (EventBundleConfig eventBundleConfig : EventBundleConfig.values()) {
      transitions.add(
        new EventBundle(
          bundleRepo.findByName(eventBundleConfig.getBundle().getName()),
          eventRepo.findByName(eventBundleConfig.getEvent().getName())
        )
      );
    }
    return transitions;
  }
}
