package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.configTest.EventConfig;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventSlotConfig {
  SLOT_THU_FRI          (OutTextConfig.LABEL_SLOT_THU_FRI_EN           .getOutTextKey(), EventConfig.REDANZ_EVENT, null,40, SlotType.ACCOMMODATION , null, null),
  SLOT_FRI_SAT          (OutTextConfig.LABEL_SLOT_FRI_SAT_EN         .getOutTextKey(), EventConfig.REDANZ_EVENT,"#00ff00", 50, SlotType.ACCOMMODATION, null, null),
  SLOT_FRIDAY_MORNING   (OutTextConfig.LABEL_SLOT_FRIDAY_MORNING_EN  .getOutTextKey(),EventConfig.REDANZ_EVENT,null , 52, SlotType.VOLUNTEER, null, null),
  SLOT_FRIDAY_AFTERNOON(OutTextConfig.LABEL_SLOT_FRIDAY_AFTERNOON_EN.getOutTextKey(),EventConfig.REDANZ_EVENT,null , 52, SlotType.VOLUNTEER, null, null),
  SLOT_FRIDAY_EVENING  (OutTextConfig.LABEL_SLOT_FRIDAY_EVENING_EN  .getOutTextKey(), EventConfig.REDANZ_EVENT,null, 54, SlotType.VOLUNTEER, null, null),
  SLOT_SAT_SUN        (OutTextConfig.LABEL_SLOT_SAT_SUN_EN          .getOutTextKey(),EventConfig.REDANZ_EVENT, "#ff8c00", 60, SlotType.ACCOMMODATION, null, null),
  SLOT_SATURDAY_LUNCH  (OutTextConfig.LABEL_SLOT_SATURDAY_LUNCH_EN  .getOutTextKey(),EventConfig.REDANZ_EVENT, null, 62, SlotType.FOOD, null, null),
  SLOT_SUNDAY_LUNCH    (OutTextConfig.LABEL_SLOT_SUNDAY_LUNCH_EN    .getOutTextKey(),EventConfig.REDANZ_EVENT, null, 74, SlotType.FOOD, null, null),
  SLOT_SUNDAY_EVENING  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_EN  .getOutTextKey(),EventConfig.REDANZ_EVENT, null,76, SlotType.VOLUNTEER , null, null),
  SLOT_SUNDAY_EVENING_AFTER_PARTY  (OutTextConfig.LABEL_SLOT_SUNDAY_EVENING_AFTER_PARTY_EN  .getOutTextKey(),EventConfig.REDANZ_EVENT, null, 78, SlotType.VOLUNTEER, null, null),
  SLOT_SUNDAY_NIGHT    (OutTextConfig.LABEL_SLOT_SUNDAY_NIGHT_EN    .getOutTextKey(), EventConfig.REDANZ_EVENT,null, 79, SlotType.VOLUNTEER, null, null),
  SLOT_SUN_MON          (OutTextConfig.LABEL_SLOT_SUN_MON_EN.getOutTextKey(), EventConfig.REDANZ_EVENT,"#ff0000", 70, SlotType.ACCOMMODATION, null, null),
  SLOT_FRIDAY          (OutTextConfig.LABEL_SLOT_FRIDAY_EN.getOutTextKey(), EventConfig.REDANZ_EVENT,"#ff0000", 70, SlotType.PARTY, null, null),
  SLOT_SATURDAY          (OutTextConfig.LABEL_SLOT_SATURDAY_EN.getOutTextKey(), EventConfig.REDANZ_EVENT,"#ff0000", 70, SlotType.PARTY, ZonedDateTime.parse("2023-07-27T11:00:00.000+01:00[Europe/Paris]"), ZonedDateTime.parse("2023-07-29T11:00:00.000+01:00[Europe/Paris]")),
  SLOT_SUNDAY          (OutTextConfig.LABEL_SLOT_SUNDAY_EN.getOutTextKey(), EventConfig.REDANZ_EVENT,"#ff0000", 70, SlotType.PARTY, null, null),;

  private final String name;
  private final EventConfig eventConfig;
  private final String color;
  private final Integer seqNr;
  private final SlotType slotType;
  private final ZonedDateTime from;
  private final ZonedDateTime to;


  public static void setup(SlotService slotService, EventService eventService) {
    for (EventSlotConfig eventSlotConfig : EventSlotConfig.values()) {
      if (!slotService.eventSlotExistsByName(eventSlotConfig.getName())) {
        slotService.save(
          new EventSlot(
            eventSlotConfig.getName(),
            eventService.findByName(eventSlotConfig.eventConfig.getName()),
            eventSlotConfig.seqNr,
            eventSlotConfig.color,
            eventSlotConfig.slotType,
            eventSlotConfig.from,
            eventSlotConfig.to
          )
        );
      } else {
        EventSlot slot = slotService.findEventSlotByName(eventSlotConfig.getName());
        slot.setColor(eventSlotConfig.color);
        slotService.save(slot);
      }
    }
  }
}
