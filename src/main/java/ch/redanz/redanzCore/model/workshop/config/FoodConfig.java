package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum FoodConfig {
  FOOD_VEGGI_ASSIAN("Veggie Asian", OutTextConfig.LABEL_FOOD_VEGGIE_ASIAN_DESC_EN.getOutTextKey(), 15),
  FOOD_SOUP("Soup", OutTextConfig.LABEL_FOOD_SOUP_DESC_EN.getOutTextKey(), 11);

  private String name;
  private String description;
  private Integer price;


  public static List<Food> setup() {
    List<Food> transitionList = new ArrayList<>();

    for (FoodConfig foodConfig : FoodConfig.values()) {
      transitionList.add(
        new Food(
          foodConfig.getName(),
          foodConfig.getPrice(),
          foodConfig.getDescription()
        )
      );
    }
    return transitionList;
  }
}
