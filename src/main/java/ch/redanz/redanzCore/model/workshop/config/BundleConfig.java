package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BundleConfig {
  HALFPASS("FUN Pass", 250, OutTextConfig.LABEL_HALFPASS_DESC_EN.getOutTextKey(), 1),
  FULLPASS("Extra FUN Pass", 320, OutTextConfig.LABEL_FULLPASS_DESC_EN.getOutTextKey(), 1),
  LEVELPASS("FUNdamentals Pass", 250, OutTextConfig.LABEL_LEVELPASS_DESC_EN.getOutTextKey(), 20),
  PARTYPASS("Party Pass", 90, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 180);

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
