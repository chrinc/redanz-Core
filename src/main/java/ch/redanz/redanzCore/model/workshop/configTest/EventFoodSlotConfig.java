package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.SlotConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventFoodSlotConfig {
  SAT_VARIETY(EventConfig.REDANZ_EVENT, FoodConfig.FOOD_VARIETY, SlotConfig.SLOT_SATURDAY_LUNCH, 25, 1),
  SUN_ORIENTAL(EventConfig.REDANZ_EVENT, FoodConfig.FOOD_ORIENTAL, SlotConfig.SLOT_SUNDAY_LUNCH, 15, 2);

  private final EventConfig eventConfig;
  private final FoodConfig foodConfig;
  private final SlotConfig slotConfig;
  private final double price;
  private final int sequence;

  public static void setup(EventService eventService, FoodService foodService, SlotService slotService) {
    for (EventFoodSlotConfig eventFoodSlotConfig : EventFoodSlotConfig.values()) {
      Event event = eventService.findByName(eventFoodSlotConfig.eventConfig.getName());
      Food food = foodService.findByName(eventFoodSlotConfig.foodConfig.getName());
      Slot slot = slotService.findByName(eventFoodSlotConfig.slotConfig.getName());
      eventService.save(new EventFoodSlot(food, slot, event, eventFoodSlotConfig.price, eventFoodSlotConfig.sequence));
    }
  }
}
