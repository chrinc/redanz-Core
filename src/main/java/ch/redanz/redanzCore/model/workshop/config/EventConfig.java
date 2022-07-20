package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum EventConfig {
  EVENT2022("Stir it! 2022", 320, "Weekend Dance Workshop");

  private final String name;
  private final Integer capacity;
  private final String description;

  EventConfig(String name, Integer capacity, String description) {
    this.name = name;
    this.capacity = capacity;
    this.description = description;
  }

  public static void setup(EventService eventService) {

    for (EventConfig trackConfig : EventConfig.values()) {
      eventService.save(
        new Event(
          trackConfig.getName(),
          trackConfig.getCapacity(),
          trackConfig.getDescription()
        )
      );
    }
  }
}
