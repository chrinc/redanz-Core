package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Event;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public enum EventConfig {
  EVENT2022("Event 2022", 300, "Weekend Dance Workshop");

  private final String name;
  private final Integer capacity;
  private final String description;

  EventConfig(String name, Integer capacity, String description) {
    this.name = name;
    this.capacity = capacity;
    this.description = description;
  }

  public static List<Event> setup() {
    List<Event> transitionList = new ArrayList<>();

    for (EventConfig trackConfig : EventConfig.values()) {
      transitionList.add(
        new Event(
          trackConfig.getName(),
          trackConfig.getCapacity(),
          trackConfig.getDescription()
        )
      );
    }
    return transitionList;
  }
}
