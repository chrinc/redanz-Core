package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackEventDiscountConfig {
  ADVANCED_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, DiscountConfig.ABROAD),
  ADVANCED_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, DiscountConfig.STUDENT),
  ADVANCED_EARLY(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, DiscountConfig.EARLY_BIRD),

  INTERMEDIATE_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, DiscountConfig.ABROAD),
  INTERMEDIATE_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, DiscountConfig.STUDENT),
  INTERMEDIATE_EARLY(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, DiscountConfig.EARLY_BIRD),

  NO_LEVEL_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, DiscountConfig.ABROAD),
  NO_LEVEL_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, DiscountConfig.STUDENT)
  ;

  private final EventConfig eventConfig;
  private final TrackConfig trackConfig;
  private final DiscountConfig discountConfig;

  public static void setup(EventService eventService, TrackService trackService, DiscountService discountService) {
    for (TrackEventDiscountConfig trackEventDiscountConfig : TrackEventDiscountConfig.values()) {
      Event event = eventService.findByName(trackEventDiscountConfig.getEventConfig().getName());
      Track track = trackService.findByName(trackEventDiscountConfig.getTrackConfig().getName());
      Discount discount = discountService.findByName(trackEventDiscountConfig.getDiscountConfig().getName());
      EventDiscount eventDiscount = eventService.findByEventAndDiscount(event, discount);
      track.getEventDiscounts().add(eventDiscount);
      trackService.save(track);
    }
  }
}
