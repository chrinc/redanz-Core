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
  VOLUNTEER_SLOT_SUNDAY_EVENING_AFTER_PARTY("volunteer", SlotConfig.SLOT_SUNDAY_EVENING_AFTER_PARTY , null),

  FOOD_SLOT_SUNDAY  ("food", SlotConfig.SLOT_SUNDAY_AFTERNOON   , FoodConfig.FOOD_ORIENTAL),
  FOOD_SLOT_SATURDAY("food", SlotConfig.SLOT_SATURDAY_LUNCH , FoodConfig.FOOD_ORIENTAL),

  ACCOMMODATION_SLOT_THURSDAY( "accommodation"    , SlotConfig.SLOT_THURSDAY    , null),
  ACCOMMODATION_SLOT_FRIDAY(   "accommodation"    , SlotConfig.SLOT_FRIDAY      , null),
  ACCOMMODATION_SLOT_SATURDAY( "accommodation"    , SlotConfig.SLOT_SATURDAY    , null),
  ACCOMMODATION_SLOT_SUNDAY(   "accommodation"    , SlotConfig.SLOT_SUNDAY      , null);

  private final String type;
  private final SlotConfig slotConfig;
  private final Object typeConfig;

  public static void setup(SlotService slotService, FoodService foodService) {
    for (TypeSlotConfig typeSlotConfig : TypeSlotConfig.values()) {

      log.info(typeSlotConfig.type + ", " + typeSlotConfig.slotConfig.getName() + ", " + typeSlotConfig.typeConfig);
      if (typeSlotConfig.getTypeConfig() != null && Objects.equals(typeSlotConfig.getTypeConfig().getClass(), FoodConfig.class)) {
        assert typeSlotConfig.getTypeConfig() instanceof FoodConfig;
        FoodConfig foodConfig = (FoodConfig) typeSlotConfig.getTypeConfig();

        if (
          !slotService.foodSlotExists(
            slotService.findByName(typeSlotConfig.getSlotConfig().getName()),
            foodService.findByName(foodConfig.getName())
          )
        ) {

          slotService.save(
            new TypeSlot(
              typeSlotConfig.getType(),
              slotService.findByName(typeSlotConfig.getSlotConfig().getName()),
              foodService.findByName(foodConfig.getName()).getFoodId()
            )
          );
        }
      } else {
        if (!slotService.existsByName(typeSlotConfig.getSlotConfig().getName())) {
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
}
