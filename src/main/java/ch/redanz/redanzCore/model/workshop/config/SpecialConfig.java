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
  SPECIAL_FRI_SPECIAL(OutTextConfig.LABEL_FRI_SPECIAL_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FRI_SPECIAL_DESC_EN.getOutTextKey(), 20, 60);

  private final String name;
  private final String description;
  private final Integer capacity;
  private final double price;

  public static void setup(SpecialService specialService) {
    for (SpecialConfig specialConfig : SpecialConfig.values()) {
      if (!specialService.existsByName(specialConfig.getName())) {
        specialService.save(
          new Special(
            specialConfig.getName(),
            specialConfig.getDescription(),
            specialConfig.price,
            specialConfig.capacity
          )
        );
      } else {
        Special special = specialService.findByName(specialConfig.getName());
        special.setDescription(specialConfig.description);
        special.setCapacity(specialConfig.capacity);
        special.setPrice(specialConfig.price);
        specialService.save(special);
      }
    }
  }
}
