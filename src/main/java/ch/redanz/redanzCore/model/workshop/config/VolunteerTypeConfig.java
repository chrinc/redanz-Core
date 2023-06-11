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
  TWO_HOURS("LABEL-VOL-TYPE-TWO-HOURS-NAME", "LABEL-VOL-TYPE-TWO-HOURS-DESC"),
  SIX_HOURS("LABEL-VOL-TYPE-SIX-HOURS-NAME", "LABEL-VOL-TYPE-SIX-HOURS-DESC"),
  NO_PREFS("LABEL-VOL-TYPE-NO-PREF-NAME", "LABEL-VOL-TYPE-NO-PREF-DESC");

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
