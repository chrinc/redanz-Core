package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.repository.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum FoodConfig {
  FOOD_VEDA(OutTextConfig.LABEL_FOOD_VEDA_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_VEDA_DESC_EN.getOutTextKey(), 20),
  FOOD_BUFFET(OutTextConfig.LABEL_FOOD_BUFFET_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_BUFFET_DESC_EN.getOutTextKey(), 25),
  FOOD_ASIAN(OutTextConfig.LABEL_FOOD_ASIAN_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_ASIAN_DESC_EN.getOutTextKey(), 10);

  private final String name;
  private final String description;
  private final Integer price;

  public static void setup(FoodService foodService) {

    for (FoodConfig foodConfig : FoodConfig.values()) {
      if (!foodService.existsByName(foodConfig.getName())) {
        foodService.save(
          new Food(
            foodConfig.getName(),
            foodConfig.getPrice(),
            foodConfig.getDescription()
          )
        );
      } else {
        Food food = foodService.findByName(foodConfig.getName());
        food.setDescription(foodConfig.description);
        food.setPrice(foodConfig.price);
        foodService.save(food);
      }
    }
  }
}
