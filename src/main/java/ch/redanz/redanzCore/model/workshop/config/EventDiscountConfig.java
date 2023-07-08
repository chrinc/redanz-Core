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
  EVENT2023_EARLY_BIRD(EventConfig.EVENT2023, DiscountConfig.EARLY_BIRD),
  EVENT2023_ABROAD(EventConfig.EVENT2023, DiscountConfig.ABROAD),
  EVENT2023_STUDENT(EventConfig.EVENT2023, DiscountConfig.STUDENT);

  private final EventConfig eventConfig;
  private final DiscountConfig discountConfig;

  EventDiscountConfig(EventConfig eventConfig, DiscountConfig discountConfig) {
    this.eventConfig = eventConfig;
    this.discountConfig = discountConfig;
  }

  public static void setup(DiscountService discountService, EventService eventService) {

    for (EventDiscountConfig eventDiscountConfig : EventDiscountConfig.values()) {
      if (!discountService.eventDiscountExists(
        eventService.findByName(eventDiscountConfig.eventConfig.getName()),
        discountService.findByName(eventDiscountConfig.discountConfig.getName())
        )
      ) {
        discountService.save(
          new EventDiscount(
            discountService.findByName(eventDiscountConfig.discountConfig.getName()),
            eventService.findByName(eventDiscountConfig.getEventConfig().getName())
          )
        );
      }
    }
  }
}
