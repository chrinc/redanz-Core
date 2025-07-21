package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventSpecial;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventSpecialsConfig {
  EVENT_MASSAGE(EventConfig.REDANZ_EVENT, SpecialConfig.SPECIAL_CHINESE_MASSAGE, 45.0, false, 30, null, true)
  ;

  private final EventConfig eventConfig;
  private final SpecialConfig specialConfig;
  private final double price;
  private final boolean soldOut;
  private final int capacity;
  private final String url;
  private final Boolean infoOnly;

  public static void setup(SpecialService specialService, EventService eventService) {
    for (EventSpecialsConfig eventSpecialsConfig : EventSpecialsConfig.values()) {
      Event event = eventService.findByName(eventSpecialsConfig.eventConfig.getName());
      Special special = specialService.findByName(eventSpecialsConfig.specialConfig.getName());
      eventService.save(new EventSpecial(
        special
        ,event
        ,eventSpecialsConfig.price
        ,eventSpecialsConfig.soldOut
        ,eventSpecialsConfig.capacity
        ,eventSpecialsConfig.url
        ,eventSpecialsConfig.infoOnly
      ));
    }
  }
}
