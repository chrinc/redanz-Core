package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventDiscountConfig {
  EVENT2022_EARLY_BIRD(EventConfig.EVENT2022, DiscountConfig.EARLY_BIRD),
  EVENT2022_LINDYANDMORE(EventConfig.EVENT2022, DiscountConfig.LINDYANDMORE),
  EVENT2022_ABROAD(EventConfig.EVENT2022, DiscountConfig.ABROAD),
  EVENT2022_STUDENT(EventConfig.EVENT2022, DiscountConfig.STUDENT);

  private final EventConfig eventConfig;
  private final DiscountConfig discountConfig;

  EventDiscountConfig(EventConfig eventConfig, DiscountConfig discountConfig) {
    this.eventConfig = eventConfig;
    this.discountConfig = discountConfig;
  }

  public static void setup(DiscountService discountService, EventService eventService) {

    for (EventDiscountConfig eventDiscountConfig : EventDiscountConfig.values()) {
      discountService.save(
        new EventDiscount(
          discountService.findByName(eventDiscountConfig.discountConfig.getName()),
          eventService.findByName(eventDiscountConfig.getEventConfig().getName())
        )
      );
    }
  }
}
