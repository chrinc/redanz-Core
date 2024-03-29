package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.repository.service.BundleService;
import ch.redanz.redanzCore.model.workshop.repository.service.SpecialService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public enum BundleSpecialConfig {
  FULL_PASS_SPECIAL(BundleConfig.FULL_PASS, SpecialConfig.FULL_PASS_SPECIAL)
  ;

  private final BundleConfig bundleConfig;
  private final SpecialConfig specialConfig;

  BundleSpecialConfig(BundleConfig bundleConfig, SpecialConfig specialConfig) {
    this.bundleConfig = bundleConfig;
    this.specialConfig = specialConfig;
  }

  public static void setup(SpecialService specialService, BundleService bundleService) {
    Map<Long, List<Special>> bundleSpecialsMap = new HashMap<>();
    for (BundleSpecialConfig bundleSpecialConfig : BundleSpecialConfig.values()) {
      Bundle bundle = bundleService.findByName(bundleSpecialConfig.bundleConfig.getName());
      Special special = specialService.findByName(bundleSpecialConfig.specialConfig.getName());
      if (bundleSpecialsMap.containsKey(bundle.getBundleId())) {
        bundleSpecialsMap.get(bundle.getBundleId()).add(special);
      } else {
        List<Special> mySpecials = new ArrayList<>();
        mySpecials.add(special);
        bundleSpecialsMap.put(bundle.getBundleId(), mySpecials);
      }
    }
    bundleSpecialsMap.keySet().forEach(bundleId -> {
      Bundle bundle = bundleService.findByBundleId(bundleId);
      bundle.setSpecials(bundleSpecialsMap.get(bundleId));
      bundleService.save(bundle);
    });
  }
}
