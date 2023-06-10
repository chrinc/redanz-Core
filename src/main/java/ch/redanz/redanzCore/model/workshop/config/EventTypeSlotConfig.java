package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventTypeSlotConfig {
  EVENT2022_ACCOM_THU(EventConfig.EVENT2022, TypeSlotConfig.ACCOMMODATION_SLOT_THURSDAY),
  EVENT2022_ACCOM_FRI(EventConfig.EVENT2022, TypeSlotConfig.ACCOMMODATION_SLOT_FRIDAY),
  EVENT2022_ACCOM_SAT(EventConfig.EVENT2022, TypeSlotConfig.ACCOMMODATION_SLOT_SATURDAY),
  EVENT2022_ACCOM_SUN(EventConfig.EVENT2022, TypeSlotConfig.ACCOMMODATION_SLOT_SUNDAY),

  EVENT2022_FOOD_FRI(EventConfig.EVENT2022, TypeSlotConfig.FOOD_SLOT_FRIDAY),
  EVENT2022_FOOD_SAT(EventConfig.EVENT2022, TypeSlotConfig.FOOD_SLOT_SATURDAY),

  EVENT2022_VOLU_FRI_AFTERNOON(EventConfig.EVENT2022, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_AFTERNOON_EN),
  EVENT2022_VOLU_FRI_EVENING(EventConfig.EVENT2022, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_EVENING),
  EVENT2022_VOLU_FRI_MORNING(EventConfig.EVENT2022, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_MORNING),
  EVENT2022_VOLU_SUN_AFTER_PARTY(EventConfig.EVENT2022, TypeSlotConfig.VOLUNTEER_SLOT_SUNDAY_EVENING_AFTER_PARTY);

  private final EventConfig eventConfig;
  private final TypeSlotConfig typeSlotConfig;

  EventTypeSlotConfig(EventConfig eventConfig, TypeSlotConfig typeSlotConfig) {
    this.eventConfig = eventConfig;
    this.typeSlotConfig = typeSlotConfig;
  }

  public static void setup(SlotService slotService, EventService eventService) {

    for (EventTypeSlotConfig eventTypeSlotConfig : EventTypeSlotConfig.values()) {
      eventService.save(
        new EventTypeSlot(
          slotService.findByTypeAndSlotName(eventTypeSlotConfig.typeSlotConfig.getType(), eventTypeSlotConfig.typeSlotConfig.getSlotConfig().getName()),
          eventService.findByName(eventTypeSlotConfig.getEventConfig().getName())
        )
      );
    }
  }
}
