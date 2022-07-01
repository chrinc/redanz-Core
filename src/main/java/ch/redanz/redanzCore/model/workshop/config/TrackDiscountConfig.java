package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackDiscountConfig {
  DISCOUNT_FUN_ABROAD(TrackConfig.FUN_TRACK, DiscountConfig.ABROAD),
  DISCOUNT_FUN_STUDENT(TrackConfig.FUN_TRACK, DiscountConfig.STUDENT),

  DISCOUNT_LINDY_ADVANCED_ABROAD(TrackConfig.LINDY_ADVANCED, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_ADVANCED_STUDENT(TrackConfig.LINDY_ADVANCED, DiscountConfig.STUDENT),

  DISCOUNT_LINDY_INTERMEDIATE_ABROAD(TrackConfig.LINDY_INTERMEDIATE, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_INTERMEDIATE_STUDENT(TrackConfig.LINDY_INTERMEDIATE, DiscountConfig.STUDENT),

  DISCOUNT_LINDY_BEGINNER_ABROAD(TrackConfig.LINDY_BEGINNER, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_BEGINNER_STUDENT(TrackConfig.LINDY_BEGINNER, DiscountConfig.STUDENT);

  private final TrackConfig trackConfig;
  private final DiscountConfig discountConfig;

  public static void setup(TrackService trackService, DiscountService discountService) {
    for (TrackDiscountConfig trackDiscountConfig : TrackDiscountConfig.values()) {
      trackService.save(
        new TrackDiscount(
          discountService.findByName(trackDiscountConfig.getDiscountConfig().getName()),
          trackService.findByName(trackDiscountConfig.getTrackConfig().getName())
        )
      );
    }
  }
}
