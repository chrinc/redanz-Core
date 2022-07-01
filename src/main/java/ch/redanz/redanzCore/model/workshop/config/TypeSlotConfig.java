package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Getter
@Slf4j
@AllArgsConstructor
public enum TypeSlotConfig {
  VOLUNTEER_SLOT_FRIDAY_AFTERNOON_EN("volunteer", SlotConfig.SLOT_FRIDAY_AFTERNOON , null),
  VOLUNTEER_SLOT_FRIDAY_MORNING("volunteer", SlotConfig.SLOT_FRIDAY_MORNING        , null),
  VOLUNTEER_SLOT_FRIDAY_EVENING("volunteer", SlotConfig.SLOT_FRIDAY_EVENING        , null),
  VOLUNTEER_SLOT_SUNDAY_EVENING("volunteer", SlotConfig.SLOT_SUNDAY_EVENING        , null),
  VOLUNTEER_SLOT_SUNDAY_NIGHT("volunteer", SlotConfig.SLOT_SUNDAY_NIGHT            , null),

  FOOD_SLOT_FRIDAY  ("food", SlotConfig.SLOT_FRIDAY   , FoodConfig.FOOD_SOUP),
  FOOD_SLOT_SATURDAY("food", SlotConfig.SLOT_SATURDAY , FoodConfig.FOOD_VEGGIE_ASIAN),
  FOOD_SLOT_SUNDAY  ("food", SlotConfig.SLOT_SUNDAY   , FoodConfig.FOOD_SOUP),

  ACCOMMODATION_SLOT_THURSDAY( "accommodation"    , SlotConfig.SLOT_THURSDAY    , null),
  ACCOMMODATION_SLOT_FRIDAY(   "accommodation"    , SlotConfig.SLOT_FRIDAY      , null),
  ACCOMMODATION_SLOT_SATURDAY( "accommodation"    , SlotConfig.SLOT_SATURDAY    , null),
  ACCOMMODATION_SLOT_SUNDAY(   "accommodation"    , SlotConfig.SLOT_SUNDAY      , null);

  private final String type;
  private final SlotConfig slotConfig;
  private final Object typeConfig;

  public static void setup(SlotService slotService, FoodService foodService) {
    for (TypeSlotConfig typeSlotConfig : TypeSlotConfig.values()) {
      if (typeSlotConfig.getTypeConfig() != null && Objects.equals(typeSlotConfig.getTypeConfig().getClass(), FoodConfig.class)) {
        assert typeSlotConfig.getTypeConfig() instanceof FoodConfig;
        FoodConfig foodConfig = (FoodConfig) typeSlotConfig.getTypeConfig();
        slotService.save(
          new TypeSlot(
            typeSlotConfig.getType(),
            slotService.findByName(typeSlotConfig.getSlotConfig().getName()),
            foodService.findByName(foodConfig.getName()).getFoodId()
          )
        );
      } else {
        slotService.save(
          new TypeSlot(
            typeSlotConfig.getType(),
            slotService.findByName(typeSlotConfig.getSlotConfig().getName())
          )
        );
      }
    }
  }
}
