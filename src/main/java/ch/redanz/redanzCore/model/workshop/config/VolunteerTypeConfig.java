package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum VolunteerTypeConfig {
  TWO_HOURS("Two Hours", "Two Hours fro two drinks"),
  SIX_HOURS("Six Hours", "Six Hours for CHF 100");

  private final String name;
  private final String description;

  public static void setup(VolunteerService volunteerService) {

    for (VolunteerTypeConfig volunteerTypeConfig : VolunteerTypeConfig.values()) {
      volunteerService.saveVolunteerType(
        new VolunteerType(
          volunteerTypeConfig.getName(),
          volunteerTypeConfig.getDescription()
        )
      );
    }
  }
}
