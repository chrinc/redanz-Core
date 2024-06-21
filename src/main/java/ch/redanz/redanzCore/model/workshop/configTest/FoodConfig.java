package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum FoodConfig {
  FOOD_ORIENTAL(OutTextConfig.LABEL_FOOD_ORIENTAL_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_ORIENTAL_DESC_EN.getOutTextKey()),
  FOOD_VARIETY(OutTextConfig.LABEL_FOOD_VARIETY_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_VARIETY_DESC_EN.getOutTextKey());

  private final String name;
  private final String description;

  public static void setup(FoodService foodService) {

    for (FoodConfig foodConfig : FoodConfig.values()) {
      if (!foodService.existsByName(foodConfig.getName())) {
        foodService.save(
          new Food(
            foodConfig.getName(),
            foodConfig.getDescription()
          )
        );
      } else {
        Food food = foodService.findByName(foodConfig.getName());
        food.setDescription(foodConfig.description);
        foodService.save(food);
      }
    }
  }
}
