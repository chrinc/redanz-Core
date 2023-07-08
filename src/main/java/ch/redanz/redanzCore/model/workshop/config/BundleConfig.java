package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleConfig {
  EXTRA_FUN_PASS("Extra FUN Pass", 330, OutTextConfig.LABEL_EXTRAFUN_DESC_EN.getOutTextKey(), 120, false, "extra_fun_pass"),
  FUN_PASS("FUN Pass", 250, OutTextConfig.LABEL_FUN_DESC_EN.getOutTextKey(), 90, false, "fun_pass"),
  PARTYPASS("Party Pass", 100, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 50, false, "party_pass"),
  PARTY_SUN("Sunday Party Ticket", 40, OutTextConfig.LABEL_PARTY_SUN_DESC_EN.getOutTextKey(), 30, true, "party_sunday"),
  FRIDAY_SPECIAL("Friday Special", 90, OutTextConfig.LABEL_FRIDAY_PACKAGE_DESC_EN.getOutTextKey(), 20, true, "friday_special")
  ;

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;
  private final Boolean simpleTicket;
  private final String internalId;

  public static void setup(BundleService bundleService) {
    for (BundleConfig bundleConfig : BundleConfig.values()) {
      if (!bundleService.existsByName(bundleConfig.getName())) {

        bundleService.save(
          new Bundle(
            bundleConfig.getName(),
            bundleConfig.getPrice(),
            bundleConfig.getDescription(),
            bundleConfig.getCapacity(),
            bundleConfig.getSimpleTicket(),
            bundleConfig.getInternalId()
          )
        );
      } else {
        Bundle bundle = bundleService.findByName(bundleConfig.name);
        bundle.setCapacity(bundleConfig.capacity);
        bundle.setDescription(bundleConfig.getDescription());
        bundle.setPrice(bundleConfig.getPrice());
        bundle.setSimpleTicket(bundleConfig.simpleTicket);
        bundle.setInternalId(bundleConfig.internalId);
        bundleService.save(bundle);
      }
    }
  }
}
