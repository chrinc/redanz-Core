package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Bundle;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum BundleConfig {
  FULLPASS("Full Pass", 320, OutTextConfig.LABEL_FULLPASS_DESC_EN.getOutTextKey(), 20),
  HALFPASS("Half Pass", 250, OutTextConfig.LABEL_HALFPASS_DESC_EN.getOutTextKey(), 20),
  PARTYPASS("Party Pass", 90, OutTextConfig.LABEL_PARTYPASS_DESC_EN.getOutTextKey(), 180);

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;

  public static List<Bundle> setup() {
    List<Bundle> transitions = new ArrayList<>();
    for (BundleConfig bundleConfig : BundleConfig.values()) {
      transitions.add(
        new Bundle(
          bundleConfig.getName(),
          bundleConfig.getPrice(),
          bundleConfig.getDescription(),
          bundleConfig.getCapacity()
        )
      );
    }
    return transitions;
  }
}
