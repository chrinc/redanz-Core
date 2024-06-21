package ch.redanz.redanzCore.model.workshop.configTest;

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
  STUDENT(OutTextConfig.LABEL_DISCOUNT_STUDENT_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_STUDENT_DESC_EN.getOutTextKey()    , 30, null),
  EARLY_BIRD(OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_DESC_EN.getOutTextKey() , 15, 30);

  private final String name;
  private final String description;
  private final double discount;
  private final Integer capacity;

  public static void setup(DiscountService discountService) {

    for (DiscountConfig discountConfig : DiscountConfig.values()) {
      if (!discountService.existsByName(discountConfig.getName())) {
        discountService.save(
          new Discount(
            discountConfig.getName(),
            discountConfig.getDescription()
          )
        );
      } else {
        Discount discount = discountService.findByName(discountConfig.getName());
        discount.setDescription(discountConfig.description);
        discountService.save(discount);
      }
    }
  }
}
