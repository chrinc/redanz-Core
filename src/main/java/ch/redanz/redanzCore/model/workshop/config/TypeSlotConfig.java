package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.TypeSlot;
import ch.redanz.redanzCore.model.workshop.repository.FoodRepo;
import ch.redanz.redanzCore.model.workshop.repository.SlotRepo;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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
  FOOD_SLOT_SATURDAY("food", SlotConfig.SLOT_SATURDAY , FoodConfig.FOOD_VEGGI_ASSIAN),
  FOOD_SLOT_SUNDAY  ("food", SlotConfig.SLOT_SUNDAY   , FoodConfig.FOOD_SOUP),

  ACCOMMODATION_SLOT_THURSDAY( "accommodation"    , SlotConfig.SLOT_THURSDAY    , null),
  ACCOMMODATION_SLOT_FRIDAY(   "accommodation"    , SlotConfig.SLOT_FRIDAY      , null),
  ACCOMMODATION_SLOT_SATURDAY( "accommodation"    , SlotConfig.SLOT_SATURDAY    , null),
  ACCOMMODATION_SLOT_SUNDAY(   "accommodation"    , SlotConfig.SLOT_SUNDAY      , null);

  private final String type;
  private final SlotConfig slotConfig;
  private final Object typeConfig;

  public static List<TypeSlot> setup(SlotRepo slotRepo, FoodRepo foodRepo) {
    List<TypeSlot> transitions = new ArrayList<>();
    for (TypeSlotConfig typeSlotConfig : TypeSlotConfig.values()) {
      if (typeSlotConfig.getTypeConfig() != null) {
      log.info("inc@TypeSlotConfig, typeSlotConfig.class?: {}", typeSlotConfig.equals(FoodConfig.class));
      log.info("inc@TypeSlotConfig, typeSlotConfig.null?: {}", typeSlotConfig.getTypeConfig());
      log.info("inc@TypeSlotConfig, getTypeConfig().getClass(): {}", typeSlotConfig.getTypeConfig().getClass());
      log.info("inc@TypeSlotConfig, FoodConfig.class?: {}", FoodConfig.class);

      }
      if (typeSlotConfig.getTypeConfig() != null && Objects.equals(typeSlotConfig.getTypeConfig().getClass(), FoodConfig.class)) {
        log.info("inc@TypeSlotConfig, typeSlotConfig.type: {}", typeSlotConfig.getType());
        FoodConfig foodConfig = (FoodConfig) typeSlotConfig.getTypeConfig();
        log.info("inc@TypeSlotConfig, foodConfig.getName(): {}", foodConfig.getName());
        log.info("inc@TypeSlotConfig, foodRepo.findByName(foodConfig.getName()): {}", foodRepo.findByName(foodConfig.getName()));
        transitions.add(
          new TypeSlot(
            typeSlotConfig.getType(),
            slotRepo.findByName(typeSlotConfig.getSlotConfig().getName()),
            foodRepo.findByName(foodConfig.getName()).getFoodId()
          )
        );
      } else {
        transitions.add(
          new TypeSlot(
            typeSlotConfig.getType(),
            slotRepo.findByName(typeSlotConfig.getSlotConfig().getName())
          )
        );
      }
    }
    return transitions;
  }
}
