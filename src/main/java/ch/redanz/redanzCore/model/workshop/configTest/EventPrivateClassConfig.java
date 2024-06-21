package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
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
public enum EventPrivateClassConfig {
  EVENT_ELZE(EventConfig.REDANZ_EVENT, PrivateClassConfig.NORMA, 90.0, false, 1);

  private final EventConfig eventConfig;
  private final PrivateClassConfig privateClassConfig;
  private final double price;
  private final boolean soldOut;
  private final int capacity;

  public static void setup(PrivateClassService privateClassService, EventService eventService) {
    for (EventPrivateClassConfig eventPrivateClassConfig : EventPrivateClassConfig.values()) {
      Event event = eventService.findByName(eventPrivateClassConfig.eventConfig.getName());
      PrivateClass privateClass = privateClassService.findByName(eventPrivateClassConfig.privateClassConfig.getName());
      eventService.save(new EventPrivateClass(privateClass, event, eventPrivateClassConfig.price, eventPrivateClassConfig.soldOut, eventPrivateClassConfig.capacity));
    }
  }
}
