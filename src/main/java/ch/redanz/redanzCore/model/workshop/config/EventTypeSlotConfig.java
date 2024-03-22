package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventTypeSlot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import ch.redanz.redanzCore.model.workshop.repository.service.EventService;
import ch.redanz.redanzCore.model.workshop.repository.service.FoodService;
import ch.redanz.redanzCore.model.workshop.repository.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventTypeSlotConfig {
  EVENT2023_ACCOM_THU(EventConfig.REDANZ_EVENT, TypeSlotConfig.ACCOMMODATION_SLOT_THURSDAY, null, SlotConfig.SLOT_THURSDAY.getSeqNr()),
  EVENT2023_ACCOM_FRI(EventConfig.REDANZ_EVENT, TypeSlotConfig.ACCOMMODATION_SLOT_FRIDAY, null, SlotConfig.SLOT_FRIDAY.getSeqNr()),
  EVENT2023_ACCOM_SAT(EventConfig.REDANZ_EVENT, TypeSlotConfig.ACCOMMODATION_SLOT_SATURDAY, null, SlotConfig.SLOT_SATURDAY.getSeqNr()),
  EVENT2023_ACCOM_SUN(EventConfig.REDANZ_EVENT, TypeSlotConfig.ACCOMMODATION_SLOT_SUNDAY, null, SlotConfig.SLOT_SUNDAY.getSeqNr() ),

  EVENT2023_PARTY_THU(EventConfig.REDANZ_EVENT, TypeSlotConfig.PARTY_FRI, null, SlotConfig.SLOT_FRIDAY.getSeqNr()),
  EVENT2023_PARTY_FRI(EventConfig.REDANZ_EVENT, TypeSlotConfig.PARTY_SAT, null, SlotConfig.SLOT_SATURDAY.getSeqNr()),
  EVENT2023_PARTY_SAT(EventConfig.REDANZ_EVENT, TypeSlotConfig.PARTY_SUN, null, SlotConfig.SLOT_SUNDAY.getSeqNr() ),

  EVENT2023_FOOD_SAT(EventConfig.REDANZ_EVENT, TypeSlotConfig.FOOD_SLOT_SATURDAY, FoodConfig.FOOD_BUFFET, SlotConfig.SLOT_SATURDAY.getSeqNr()),
  EVENT2023_FOOD_SUN(EventConfig.REDANZ_EVENT, TypeSlotConfig.FOOD_SLOT_SUNDAY, FoodConfig.FOOD_ASIAN, SlotConfig.SLOT_SUNDAY.getSeqNr()),

  EVENT2023_VOLU_FRI_AFTERNOON(EventConfig.REDANZ_EVENT, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_AFTERNOON_EN, null, SlotConfig.SLOT_FRIDAY_AFTERNOON.getSeqNr()),
  EVENT2023_VOLU_FRI_EVENING(EventConfig.REDANZ_EVENT, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_EVENING, null, SlotConfig.SLOT_FRIDAY_EVENING.getSeqNr()),
  EVENT2023_VOLU_FRI_MORNING(EventConfig.REDANZ_EVENT, TypeSlotConfig.VOLUNTEER_SLOT_FRIDAY_MORNING, null,SlotConfig.SLOT_FRIDAY_MORNING.getSeqNr()),
  EVENT2023_VOLU_SUN_AFTER_PARTY(EventConfig.REDANZ_EVENT, TypeSlotConfig.VOLUNTEER_SLOT_SUNDAY_EVENING_AFTER_PARTY, null, SlotConfig.SLOT_SUNDAY_EVENING_AFTER_PARTY.getSeqNr());

  private final EventConfig eventConfig;
  private final TypeSlotConfig typeSlotConfig;
  private final Object typeConfig;
//  private final SlotConfig slotConfig;
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
        if (!slotService.existsEventSlotType(
          event, foodTypeSlot
        )) {
          slotService.save(
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
        if (!slotService.existsEventSlotType(
          event, typeSlot
        )) {
          slotService.save(
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
