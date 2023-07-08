package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

@Slf4j
@Getter
public enum EventConfig {
  EVENT2023(
  "Stir it! 2023"
  , 350
  , LocalDate.parse("2023-11-17")
  , LocalDate.parse("2023-11-19")
    ,ZonedDateTime.parse("2023-07-19T10:00:00.000+01:00[Europe/Paris]")
  ,true
    ,false
  ,"Weekend Dance Workshop"
    ,"stirit2023");

  private final String name;
  private final Integer capacity;
  private final LocalDate dateFrom;
  private final LocalDate dateTo;
  private final ZonedDateTime registrationStart;
  private final Boolean active;
  private final Boolean archived;
  private final String description;
  private final String internalId;

  EventConfig(String name, Integer capacity, LocalDate dateFrom, LocalDate dateTo, ZonedDateTime registrationStart, boolean active, boolean archived, String description, String internalId) {
    this.name = name;
    this.capacity = capacity;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.registrationStart = registrationStart;
    this.active = active;
    this.archived = archived;
    this.description = description;
    this.internalId = internalId;
  }

  public static void setup(EventService eventService) {

    for (EventConfig eventConfig : EventConfig.values()) {
      if (!eventService.existsByName(eventConfig.name)) {
        eventService.save(
          new Event(
            eventConfig.name,
            eventConfig.capacity,
            eventConfig.dateFrom,
            eventConfig.dateTo,
            eventConfig.registrationStart,
            eventConfig.active,
            eventConfig.archived,
            eventConfig.description,
            eventConfig.internalId
          )
        );
      } else {
        Event event = eventService.findByName(eventConfig.name);
        event.setEventTo(eventConfig.dateTo);
        event.setEventFrom(eventConfig.dateFrom);
        event.setActive(eventConfig.active);
        event.setCapacity(eventConfig.capacity);
        event.setArchived(eventConfig.archived);
        event.setDescription(eventConfig.description);
        event.setRegistrationStart(eventConfig.registrationStart);
        event.setInternalId(eventConfig.internalId);
        eventService.save(event);
      }

    }
  }
}
