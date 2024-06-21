package ch.redanz.redanzCore.model.workshop.configTest;

//import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventDiscountConfig {
  EVENT_EARLY_BIRD(EventConfig.REDANZ_EVENT, DiscountConfig.EARLY_BIRD, 25, 30.0),
  EVENT_ABROAD(EventConfig.REDANZ_EVENT, DiscountConfig.ABROAD, null, 45.0),
  EVENT_STUDENT(EventConfig.REDANZ_EVENT, DiscountConfig.STUDENT, null, 15.0);

  private final EventConfig eventConfig;
  private final DiscountConfig discountConfig;
  private final Integer capacity;
  private final Double amount;

  public static void setup(DiscountService discountService, EventService eventService) {
    for (EventDiscountConfig eventDiscountConfig : EventDiscountConfig.values()) {
      Event event = eventService.findByName(eventDiscountConfig.getEventConfig().getName());
      Discount discount = discountService.findByName(eventDiscountConfig.getDiscountConfig().getName());
      eventService.save(new EventDiscount(discount, event, eventDiscountConfig.getAmount(), eventDiscountConfig.getCapacity()));
    }
  }

}
