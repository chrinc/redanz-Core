package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum FoodConfig {
  FOOD_VEGGIE_ASIAN("Veggie Asian", OutTextConfig.LABEL_FOOD_VEGGIE_ASIAN_DESC_EN.getOutTextKey(), 15),
  FOOD_SOUP("Soup", OutTextConfig.LABEL_FOOD_SOUP_DESC_EN.getOutTextKey(), 11);

  private final String name;
  private final String description;
  private final Integer price;

  public static void setup(FoodService foodService) {

    for (FoodConfig foodConfig : FoodConfig.values()) {
      foodService.save(
        new Food(
          foodConfig.getName(),
          foodConfig.getPrice(),
          foodConfig.getDescription()
        )
      );
    }
  }
}
