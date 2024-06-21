package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum VolunteerTypeConfig {
  TWO_HOURS(OutTextConfig.LABEL_VOL_TYPE_FOUR_HOURS_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_FOUR_HOURS_DESC_EN.getOutTextKey()),
  FIVE_HOURS(OutTextConfig.LABEL_VOL_TYPE_FIVE_HOURS_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_FIVE_HOURS_DESC_EN.getOutTextKey()),
  NO_PREFS(OutTextConfig.LABEL_VOL_TYPE_NO_PREF_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_NO_PREF_DESC_EN.getOutTextKey());

  private final String name;
  private final String description;

  public static void setup(VolunteerService volunteerService) {
    for (VolunteerTypeConfig volunteerTypeConfig : VolunteerTypeConfig.values()) {
      if (!volunteerService.existsByName(volunteerTypeConfig.getName())) {
        volunteerService.saveVolunteerType(
          new VolunteerType(
            volunteerTypeConfig.getName(),
            volunteerTypeConfig.getDescription()
          )
        );
      }
    }
  }
}
