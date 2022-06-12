package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Bundle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum BundleConfig {
  FULLPASS("Full Pass", 320, "10 Classes on Friday, Saturday, Sunday & 3 Parties", 20),
  HALFPASS("Half Pass", 250, "10 Free-Choice afternoon classes on Friday, Saturday, Sunday & 3 Parties", 20),
  PARTYPASS("Party Pass", 90, "3 Parties", 180);

  private final String name;
  private final Integer price;
  private final String description;
  private final Integer capacity;

  BundleConfig(String name, Integer price, String description, Integer capacity) {
    this.name = name;
    this.price = price;
    this.description = description;
    this.capacity = capacity;
  }

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
