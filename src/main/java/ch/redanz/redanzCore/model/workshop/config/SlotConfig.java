package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum SlotConfig {
  SLOT_THURSDAY        (OutTextConfig.LABEL_SLOT_THURSDAY_EN        .getOutTextKey()),
  SLOT_FRIDAY_MORNING  (OutTextConfig.LABEL_SLOT_FRIDAY_MORNING_EN  .getOutTextKey()),
  SLOT_FRIDAY_AFTERNOON(OutTextConfig.LABEL_SLOT_FRIDAY_AFTERNOON_EN.getOutTextKey()),
  SLOT_FRIDAY_EVENING  (OutTextConfig.LABEL_SLOT_FRIDAY_EVENING_EN  .getOutTextKey()),
  SLOT_FRIDAY          (OutTextConfig.LABEL_SLOT_FRIDAY_EN          .getOutTextKey()),
  SLOT_SATURDAY        (OutTextConfig.LABEL_SLOT_SATURDAY_EN        .getOutTextKey()),
  SLOT_SATURDAY_LUNCH  (OutTextConfig.LABEL_SLOT_SATURDAY_LUNCH_EN  .getOutTextKey()),
  SLOT_SUNDAY          (OutTextConfig.LABEL_SLOT_SUNDAY_EN          .getOutTextKey()),

  SLOT_SUNDAY_EVENING  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_EN  .getOutTextKey()),
  SLOT_SUNDAY_AFTERNOON  (OutTextConfig.LABEL_SLOT_SUNDAY_AFTERNOON_EN  .getOutTextKey()),
  SLOT_SUNDAY_EVENING_AFTER_PARTY  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_AFTER_PARTY_EN  .getOutTextKey()),
  SLOT_SUNDAY_NIGHT    (OutTextConfig.LABEL_SLOT_SUNDAY_NIGHT_EN    .getOutTextKey());

  private final String name;

  public static void setup(SlotService slotService) {
    for (SlotConfig slotConfig : SlotConfig.values()) {
      if (!slotService.existsByName(slotConfig.getName())) {
        slotService.save(
          new Slot(
            slotConfig.getName()
          )
        );
      }
    }
  }
}
