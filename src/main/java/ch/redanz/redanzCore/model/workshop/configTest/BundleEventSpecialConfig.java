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
  FULL_MASSAGE(EventConfig.REDANZ_EVENT, SpecialConfig.SPECIAL_CHINESE_MASSAGE, BundleConfig.FULL_PASS),
  HALF_MASSAGE(EventConfig.REDANZ_EVENT, SpecialConfig.SPECIAL_CHINESE_MASSAGE, BundleConfig.HALF_PASS),
  ;

  private final EventConfig eventConfig;
  private final SpecialConfig specialConfig;
  private final BundleConfig bundleConfig;

  public static void setup(EventService eventService, SpecialService specialService, BundleService bundleService) {
    for (BundleEventSpecialConfig bundleEventSpecialConfig : BundleEventSpecialConfig.values()) {
      Event event = eventService.findByName(bundleEventSpecialConfig.getEventConfig().getName());
      Bundle bundle = bundleService.findByName(bundleEventSpecialConfig.getBundleConfig().getName());
      Special special = specialService.findByName(bundleEventSpecialConfig.getSpecialConfig().getName());
      EventSpecial eventSpecial = eventService.findByEventAndSpecial(event, special);
      bundle.getEventSpecials().add(eventSpecial);
      bundleService.save(bundle);
    }
  }
}
