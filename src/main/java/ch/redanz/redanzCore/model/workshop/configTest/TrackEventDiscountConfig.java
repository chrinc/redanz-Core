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
  ADVANCED_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, EventDiscountConfig.ABROAD),
  ADVANCED_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, EventDiscountConfig.STUDENT),
  ADVANCED_EARLY(EventConfig.REDANZ_EVENT, TrackConfig.ADVANCED, EventDiscountConfig.EARLY_BIRD),

  INTERMEDIATE_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, EventDiscountConfig.ABROAD),
  INTERMEDIATE_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, EventDiscountConfig.STUDENT),
  INTERMEDIATE_EARLY(EventConfig.REDANZ_EVENT, TrackConfig.INTERMEDIATE, EventDiscountConfig.EARLY_BIRD),

  NO_LEVEL_ABROAD(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, EventDiscountConfig.ABROAD),
  NO_LEVEL_STUDENT(EventConfig.REDANZ_EVENT, TrackConfig.NO_LEVEL, EventDiscountConfig.STUDENT)
  ;

  private final EventConfig eventConfig;
  private final TrackConfig trackConfig;
  private final EventDiscountConfig eventDiscountConfig;

  public static void setup(EventService eventService, TrackService trackService, DiscountService discountService) {
    for (TrackEventDiscountConfig trackEventDiscountConfig : TrackEventDiscountConfig.values()) {
      Event event = eventService.findByName(trackEventDiscountConfig.getEventConfig().getName());
      Track track = trackService.findByName(trackEventDiscountConfig.getTrackConfig().getName());
      EventDiscount eventDiscount = discountService.findByName(trackEventDiscountConfig.getEventConfig().getName());
      track.getEventDiscounts().add(eventDiscount);
      trackService.save(track);
    }
  }
}
