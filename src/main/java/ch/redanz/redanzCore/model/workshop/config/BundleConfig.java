package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BundleConfig {
  HALFPASS("Regular Bundle", 250, OutTextConfig.LABEL_HALFPASS_DESC_EN.getOutTextKey(), 40),
  FULLPASS("Large Bundle", 350, OutTextConfig.LABEL_FULLPASS_DESC_EN.getOutTextKey(), 110),
  LEVELPASS("Level Bundle", 350, OutTextConfig.LABEL_LEVELPASS_DESC_EN.getOutTextKey(), 110),
  PARTYPASS("Party Bundle", 90, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 30);

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;

  public static void setup(BundleService bundleService) {
    for (BundleConfig bundleConfig : BundleConfig.values()) {
      bundleService.save(
        new Bundle(
          bundleConfig.getName(),
          bundleConfig.getPrice(),
          bundleConfig.getDescription(),
          bundleConfig.getCapacity()
        )
      );
    }
  }
}
