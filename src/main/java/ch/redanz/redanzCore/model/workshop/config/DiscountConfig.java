package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum DiscountConfig {
  ABROAD(OutTextConfig.LABEL_DISCOUNT_ABROAD_EN.getOutTextKey()  , OutTextConfig.LABEL_DISCOUNT_ABROAD_DESC_EN.getOutTextKey(), 50, null),
  LINDYANDMORE(OutTextConfig.LABEL_DISCOUNT_LINDYANDMORE_EN.getOutTextKey()  , OutTextConfig.LABEL_DISCOUNT_LINDYANDMORE_DESC_EN.getOutTextKey(), 15, null),
  STUDENT(OutTextConfig.LABEL_DISCOUNT_STUDENT_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_STUDENT_DESC_EN.getOutTextKey()    , 30, null),
  EARLY_BIRD(OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_DESC_EN.getOutTextKey() , 15, 30);

  private final String name;
  private final String description;
  private final double discount;
  private final Integer capacity;

  public static void setup(DiscountService discountService) {

    for (DiscountConfig discountConfig : DiscountConfig.values()) {
      discountService.save(
        new Discount(
          discountConfig.getName(),
          discountConfig.getDiscount(),
          discountConfig.getDescription(),
          discountConfig.getCapacity()
        )
      );
    }
  }
}
