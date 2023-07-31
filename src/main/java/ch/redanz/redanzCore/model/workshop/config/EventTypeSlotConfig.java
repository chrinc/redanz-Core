package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.FoodService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventTypeSlotConfig {
  EVENT2023_ACCOM_THU(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_THURSDAY, null, 1),
  EVENT2023_ACCOM_FRI(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_FRIDAY, null, 2),
  EVENT2023_ACCOM_SAT(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_SATURDAY, null,3),
  EVENT2023_ACCOM_SUN(EventConfig.EVENT2023, TypeSlotConfig.ACCOMMODATION_SLOT_SUNDAY, null,4 ),

  EVENT2023_PARTY_THU(EventConfig.EVENT2023, TypeSlotConfig.PARTY_FRI, null, 1),
  EVENT2023_PARTY_FRI(EventConfig.EVENT2023, TypeSlotConfig.PARTY_SAT, null, 2),
  EVENT2023_PARTY_SAT(EventConfig.EVENT2023, TypeSlotConfig.PARTY_SUN, null,3),

  EVENT2023_FOOD_SAT(EventConfig.EVENT2023, TypeSlotConfig.FOOD_SLOT_SATURDAY, FoodConfig.FOOD_ORIENTAL, 1),
  EVENT2023_FOOD_SUN(EventConfig.EVENT2023, TypeSlotConfig.FOOD_SLOT_SUNDAY, FoodConfig.FOOD_VARIETY, 2),

  EVENT2023_VOLU_FRI_AFTERNOON(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_AFTERNOON_EN, null, 1),
  EVENT2023_VOLU_FRI_EVENING(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_EVENING, null, 2),
  EVENT2023_VOLU_FRI_MORNING(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_MORNING, null,3),
  EVENT2023_VOLU_SUN_AFTER_PARTY(EventConfig.EVENT2023, TypeSlotConfig.VOLUNTEER_SLOT_SUNDAY_EVENING_AFTER_PARTY, null, 4);

  private final EventConfig eventConfig;
  private final TypeSlotConfig typeSlotConfig;
  private final Object typeConfig;
  private final Integer seqNr;

  public static void setup(SlotService slotService, EventService eventService, FoodService foodService) {
    for (EventTypeSlotConfig eventTypeSlotConfig : EventTypeSlotConfig.values()) {
      boolean foodType = eventTypeSlotConfig.getTypeConfig() != null && Objects.equals(eventTypeSlotConfig.getTypeConfig().getClass(), FoodConfig.class);
      Event event = eventService.findByName(eventTypeSlotConfig.eventConfig.getName());

      if (foodType) {
        FoodConfig foodConfig = (FoodConfig) eventTypeSlotConfig.getTypeConfig();
        TypeSlot foodTypeSlot = slotService.findByTypeObjectIdAndSlot(
          eventTypeSlotConfig.typeSlotConfig.getType(),
          foodService.findByName(foodConfig.getName()).getFoodId()
          , slotService.findByName(eventTypeSlotConfig.typeSlotConfig.getSlotConfig().getName())
        );
        if (!eventService.existsEventSlotType(
          event, foodTypeSlot
        )) {
          eventService.save(
            new EventTypeSlot(
              foodTypeSlot,
              event,
              eventTypeSlotConfig.seqNr
            )
          );
        }
      } else {
        TypeSlot typeSlot = slotService.findByTypeAndSlotName(
          eventTypeSlotConfig.typeSlotConfig.getType()
          , eventTypeSlotConfig.typeSlotConfig.getSlotConfig().getName()
        );
        if (!eventService.existsEventSlotType(
          event, typeSlot
        )) {
          eventService.save(
            new EventTypeSlot(
              typeSlot,
              event,
              eventTypeSlotConfig.seqNr
            )
          );
        }
      }
    }
  }
}
