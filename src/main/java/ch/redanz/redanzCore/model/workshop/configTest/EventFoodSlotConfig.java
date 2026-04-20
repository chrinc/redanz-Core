package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.EventSlotConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventFoodSlotConfig {
  SAT_VARIETY(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_FOOD_ORIENTAL_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_ORIENTAL_DESC_EN.getOutTextKey(), EventSlotConfig.SLOT_SATURDAY_LUNCH, 25, 1),
  SUN_ORIENTAL(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_FOOD_VARIETY_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_FOOD_VARIETY_DESC_EN.getOutTextKey(), EventSlotConfig.SLOT_SUNDAY_LUNCH, 15, 2);

  private final EventConfig eventConfig;
  private final String name;
  private final String description;
  private final EventSlotConfig eventSlotConfig;
  private final double price;
  private final int sequence;

  public static void setup(EventService eventService, SlotService slotService) {
    for (EventFoodSlotConfig eventFoodSlotConfig : EventFoodSlotConfig.values()) {
      Event event = eventService.findByName(eventFoodSlotConfig.eventConfig.getName());
      EventSlot eventSlot = slotService.findEventSlotByName(eventFoodSlotConfig.eventSlotConfig.getName());
      eventService.save(new EventFoodSlot(eventFoodSlotConfig.name, eventFoodSlotConfig.description, eventSlot, event, eventFoodSlotConfig.price, eventFoodSlotConfig.sequence));
    }
  }
}
