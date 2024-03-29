package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.repository.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum SlotConfig {
  SLOT_THURSDAY        (OutTextConfig.LABEL_SLOT_THURSDAY_EN        .getOutTextKey(), null, 40),
  SLOT_FRIDAY          (OutTextConfig.LABEL_SLOT_FRIDAY_EN          .getOutTextKey(), "#00ff00", 50),
  SLOT_FRIDAY_MORNING  (OutTextConfig.LABEL_SLOT_FRIDAY_MORNING_EN  .getOutTextKey(), null, 52),
  SLOT_FRIDAY_AFTERNOON(OutTextConfig.LABEL_SLOT_FRIDAY_AFTERNOON_EN.getOutTextKey(), null, 54),
  SLOT_FRIDAY_EVENING  (OutTextConfig.LABEL_SLOT_FRIDAY_EVENING_EN  .getOutTextKey(), null,56),
  SLOT_SATURDAY        (OutTextConfig.LABEL_SLOT_SATURDAY_EN        .getOutTextKey(), "#ff8c00", 60),
  SLOT_SATURDAY_LUNCH  (OutTextConfig.LABEL_SLOT_FOOD_SATURDAY_LUNCH_EN .getOutTextKey(), null, 62),
  SLOT_SUNDAY_LUNCH    (OutTextConfig.LABEL_SLOT_FOOD_SUNDAY_AFTERNOON_EN .getOutTextKey(), null, 74),

  SLOT_SUNDAY_EVENING  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_EN  .getOutTextKey(), null,76),
  SLOT_SUNDAY_EVENING_AFTER_PARTY  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_AFTER_PARTY_EN  .getOutTextKey(), null, 78),
  SLOT_SUNDAY_NIGHT    (OutTextConfig.LABEL_SLOT_SUNDAY_NIGHT_EN    .getOutTextKey(), null, 79),
  SLOT_SUNDAY          (OutTextConfig.LABEL_SLOT_SUNDAY_EN.getOutTextKey(), "#ff0000", 70);

  private final String name;
  private final String color;
  private final Integer seqNr;

  public static void setup(SlotService slotService) {
    for (SlotConfig slotConfig : SlotConfig.values()) {
      if (!slotService.existsByName(slotConfig.getName())) {
        slotService.save(
          new Slot(
            slotConfig.getName(),
            slotConfig.color,
            slotConfig.seqNr
          )
        );
      } else {
        Slot slot = slotService.findByName(slotConfig.getName());
        slot.setColor(slotConfig.color);
        slotService.save(slot);
      }
    }
  }
}
