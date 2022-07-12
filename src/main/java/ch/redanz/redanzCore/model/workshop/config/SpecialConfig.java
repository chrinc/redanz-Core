package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum SpecialConfig {
  SPECIAL_MASSAGE(OutTextConfig.LABEL_SPECIAL_MASSAGE_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_SPECIAL_MASSAGE_DESC_EN.getOutTextKey());

  private final String name;
  private final String description;

  public static void setup(SpecialService specialService) {
    for (SpecialConfig specialConfig : SpecialConfig.values()) {
      specialService.save(
        new Special(
          specialConfig.getName(),
          specialConfig.getDescription()
        )
      );
    }
  }
}
