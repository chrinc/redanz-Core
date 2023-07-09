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
  FOOD_VEDA(OutTextConfig.LABEL_FOOD_VEDA_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_VEDA_DESC_EN.getOutTextKey(), 20),
  FOOD_ORIENTAL(OutTextConfig.LABEL_FOOD_ORIENTAL_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_ORIENTAL_DESC_EN.getOutTextKey(), 20);

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
