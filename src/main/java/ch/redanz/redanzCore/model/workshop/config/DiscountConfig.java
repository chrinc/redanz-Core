package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Discount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum DiscountConfig {
  ABROAD(OutTextConfig.LABEL_DISCOUNT_ABROAD_EN.getOutTextKey()  , OutTextConfig.LABEL_DISCOUNT_ABROAD_DESC_EN.getOutTextKey(), 15),
  STUDENT(OutTextConfig.LABEL_DISCOUNT_STUDENT_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_STUDENT_DESC_EN.getOutTextKey()    , 15),
  EARLY_BIRD(OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_EN.getOutTextKey(), OutTextConfig.LABEL_DISCOUNT_EARLY_BIRD_DESC_EN.getOutTextKey() , 15);

  private final String name;
  private final String description;
  private final double discount;

  public static List<Discount> setup() {
    List<Discount> transitionList = new ArrayList<>();

    for (DiscountConfig discountConfig : DiscountConfig.values()) {
      transitionList.add(
        new Discount(
          discountConfig.getName(),
          discountConfig.getDiscount(),
          discountConfig.getDescription()
        )
      );
    }
    return transitionList;
  }
}
