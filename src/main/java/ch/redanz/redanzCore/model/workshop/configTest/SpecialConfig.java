package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum SpecialConfig {
  SPECIAL_CHINESE_MASSAGE(OutTextConfig.LABEL_SPECIAL_CHINESE_MASSAGE_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_SPECIAL_CHINESE_MASSAGE_DESC_EN.getOutTextKey());

  private final String name;
  private final String description;

  public static void setup(SpecialService specialService) {
    for (SpecialConfig specialConfig : SpecialConfig.values()) {
      if (!specialService.existsByName(specialConfig.getName())) {
        specialService.save(
          new Special(
            specialConfig.getName(),
            specialConfig.getDescription()
          )
        );
      } else {
        Special special = specialService.findByName(specialConfig.getName());
        special.setDescription(specialConfig.description);
        specialService.save(special);
      }
    }
  }
}
