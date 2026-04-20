package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
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
  EARLY_BIRD(EventConfig.REDANZ_EVENT, 25, 30.0, OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_DESC_EN.getOutTextKey()),
  ABROAD(EventConfig.REDANZ_EVENT, null, 45.0, OutTextConfig.LABEL_DISCOUNT_ABROAD_EN.getOutTextKey()  , OutTextConfig.LABEL_DISCOUNT_ABROAD_DESC_EN.getOutTextKey()),
  STUDENT(EventConfig.REDANZ_EVENT, null, 15.0, OutTextConfig.LABEL_DISCOUNT_STUDENT_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_STUDENT_DESC_EN.getOutTextKey());


  private final EventConfig eventConfig;
  private final Integer capacity;
  private final Double amount;
  private final String name;
  private final String description;

  public static void setup(DiscountService discountService, EventService eventService) {
    for (EventDiscountConfig eventDiscountConfig : EventDiscountConfig.values()) {
      Event event = eventService.findByName(eventDiscountConfig.getEventConfig().getName());
      eventService.save(new EventDiscount(event, eventDiscountConfig.getAmount(), eventDiscountConfig.getCapacity(), eventDiscountConfig.getName(), eventDiscountConfig.getDescription()));
    }
  }

}
