package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Discount;
import ch.redanz.redanzCore.model.workshop.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.DiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackDiscountConfig {
  DISCOUNT_FUN_ABROAD(TrackConfig.FUN_TRACK, DiscountConfig.ABROAD),
  DISCOUNT_FUN_STUDENT(TrackConfig.FUN_TRACK, DiscountConfig.STUDENT),
//  DISCOUNT_FUN_EARLY_BIRD(TrackConfig.FUN_TRACK, DiscountConfig.EARLY_BIRD),

  DISCOUNT_LINDY_ADVANCED_ABROAD(TrackConfig.LINDY_ADVANCED, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_ADVANCED_STUDENT(TrackConfig.LINDY_ADVANCED, DiscountConfig.STUDENT),
//  DISCOUNT_LINDY_ADVANCED_EARLY_BIRD(TrackConfig.LINDY_ADVANCED, DiscountConfig.EARLY_BIRD),

  DISCOUNT_LINDY_INTERMEDIATE_ABROAD(TrackConfig.LINDY_INTERMEDIATE, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_INTERMEDIATE_STUDENT(TrackConfig.LINDY_INTERMEDIATE, DiscountConfig.STUDENT),
//  DISCOUNT_LINDY_INTERMEDIATE_EARLY_BIRD(TrackConfig.LINDY_INTERMEDIATE, DiscountConfig.EARLY_BIRD),

  DISCOUNT_LINDY_BEGINNER_ABROAD(TrackConfig.LINDY_BEGINNER, DiscountConfig.ABROAD),
  DISCOUNT_LINDY_BEGINNER_STUDENT(TrackConfig.LINDY_BEGINNER, DiscountConfig.STUDENT);
//  DISCOUNT_LINDY_BEGINNER_EARLY_BIRD(TrackConfig.LINDY_BEGINNER, DiscountConfig.EARLY_BIRD);

  private final TrackConfig trackConfig;
  private final DiscountConfig discountConfig;

  public static List<TrackDiscount> setup(TrackRepo trackRepo, DiscountRepo discountRepo) {
    List<TrackDiscount> transitions = new ArrayList<>();

    for (TrackDiscountConfig trackDiscountConfig : TrackDiscountConfig.values()) {
      transitions.add(
        new TrackDiscount(
          discountRepo.findByName(trackDiscountConfig.getDiscountConfig().getName()),
          trackRepo.findByName(trackDiscountConfig.getTrackConfig().getName())
        )
      );
    }
    return transitions;
  }
}
