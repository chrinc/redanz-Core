package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventTypeSlotConfig {
  EVENT2023_ACCOM_THU(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_THURSDAY),
  EVENT2023_ACCOM_FRI(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_FRIDAY),
  EVENT2023_ACCOM_SAT(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_SATURDAY),
  EVENT2023_ACCOM_SUN(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_SUNDAY),

  EVENT2023_FOOD_FRI(EventConfig.EVENT2023, TypeSlotConfig.FOOD_SLOT_FRIDAY),
  EVENT2023_VOLU_FRI_AFTERNOON(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_AFTERNOON_EN),
  EVENT2023_VOLU_FRI_EVENING(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_EVENING),
  EVENT2023_VOLU_FRI_MORNING(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_MORNING),
  EVENT2023_VOLU_SUN_AFTER_PARTY(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_SUNDAY_EVENING_AFTER_PARTY);

  private final EventConfig eventConfig;
  private final TypeSlotConfig typeSlotConfig;

  EventTypeSlotConfig(EventConfig eventConfig, TypeSlotConfig typeSlotConfig) {
    this.eventConfig = eventConfig;
    this.typeSlotConfig = typeSlotConfig;
  }

  public static void setup(SlotService slotService, EventService eventService) {
    for (EventTypeSlotConfig eventTypeSlotConfig : EventTypeSlotConfig.values()) {
      if (!eventService.existsEventSlotType(
        eventService.findByName(eventTypeSlotConfig.eventConfig.getName())
       ,slotService.findByTypeAndSlotName(eventTypeSlotConfig.typeSlotConfig.getType(), eventTypeSlotConfig.typeSlotConfig.getSlotConfig().getName()))
      )
      eventService.save(
        new EventTypeSlot(
          slotService.findByTypeAndSlotName(eventTypeSlotConfig.typeSlotConfig.getType(), eventTypeSlotConfig.typeSlotConfig.getSlotConfig().getName()),
          eventService.findByName(eventTypeSlotConfig.getEventConfig().getName())
        )
      );
    }
  }
}
