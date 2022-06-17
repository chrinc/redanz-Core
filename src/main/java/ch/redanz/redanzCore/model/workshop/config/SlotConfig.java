package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Slot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
  SLOT_SUNDAY          (OutTextConfig.LABEL_SLOT_SUNDAY_EN          .getOutTextKey()),
  SLOT_SUNDAY_EVENING  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_EN  .getOutTextKey()),
  SLOT_SUNDAY_NIGHT    (OutTextConfig.LABEL_SLOT_SUNDAY_NIGHT_EN    .getOutTextKey());

  private final String name;

  public static List<Slot> setup() {
    List<Slot> transitionList = new ArrayList<>();

    for (SlotConfig slotConfig : SlotConfig.values()) {
      transitionList.add(
        new Slot(
          slotConfig.getName()
        )
      );
    }
    return transitionList;
  }
}
