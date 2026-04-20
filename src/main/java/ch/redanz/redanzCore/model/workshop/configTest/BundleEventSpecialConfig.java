package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleEventSpecialConfig {
  FULL_MASSAGE(EventConfig.REDANZ_EVENT, EventSpecialsConfig.EVENT_MASSAGE, BundleConfig.FULL_PASS),
  HALF_MASSAGE(EventConfig.REDANZ_EVENT, EventSpecialsConfig.EVENT_MASSAGE, BundleConfig.HALF_PASS),
  HALF_MAGAZINE_DESIGN(EventConfig.REDANZ_EVENT, EventSpecialsConfig.EVENT_MAGAZINE_DESIGN, BundleConfig.HALF_PASS),
  HALF_BRUNCH(EventConfig.REDANZ_EVENT, EventSpecialsConfig.EVENT_BRUNCH, BundleConfig.HALF_PASS),
  ;

  private final EventConfig eventConfig;
  private final EventSpecialsConfig eventSpecialsConfig;
  private final BundleConfig bundleConfig;

  public static void setup(EventService eventService, SpecialService specialService, BundleService bundleService) {
    for (BundleEventSpecialConfig bundleEventSpecialConfig : BundleEventSpecialConfig.values()) {
      Event event = eventService.findByName(bundleEventSpecialConfig.getEventConfig().getName());
      Bundle bundle = bundleService.findByName(bundleEventSpecialConfig.getBundleConfig().getName());
      EventSpecial eventSpecial = specialService.findByName(bundleEventSpecialConfig.getEventSpecialsConfig().getName());
      bundle.getEventSpecials().add(eventSpecial);
      bundleService.save(bundle);
    }
  }
}
