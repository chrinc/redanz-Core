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
  EVENT_ELZE(EventConfig.REDANZ_EVENT, "Norma", OutTextConfig.LABEL_PRIVATE_NORMA_DESC_EN.getOutTextKey(), false, 90.0, false, 1);

  private final EventConfig eventConfig;
  private final String name;
  private final String description;
  private final boolean partnerRequired;
  private final double price;
  private final boolean soldOut;
  private final int capacity;

  public static void setup(PrivateClassService privateClassService, EventService eventService) {
    for (EventPrivateClassConfig eventPrivateClassConfig : EventPrivateClassConfig.values()) {
      Event event = eventService.findByName(eventPrivateClassConfig.eventConfig.getName());
      eventService.save(new EventPrivateClass(event, eventPrivateClassConfig.price, eventPrivateClassConfig.soldOut, eventPrivateClassConfig.capacity, eventPrivateClassConfig.name, eventPrivateClassConfig.description, eventPrivateClassConfig.partnerRequired));
    }
  }
}
