package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Slf4j
@Getter
public enum EventConfig {
  REDANZ_EVENT(
  "Redanz Workshop"
  , 350
  , LocalDate.parse("2025-11-01")
  , LocalDate.parse("2025-11-04")
    ,ZonedDateTime.parse("2023-07-29T11:00:00.000+01:00[Europe/Paris]")
  ,true
    ,false
  ,"Weekend Dance Workshop",
    true,
    true,
    true
  );

  private final String name;
  private final Integer capacity;
  private final LocalDate dateFrom;
  private final LocalDate dateTo;
  private final ZonedDateTime registrationStart;
  private final Boolean active;
  private final Boolean archived;
  private final String description;
  private final Boolean hosting;
  private final Boolean volunteering;
  private final Boolean scholarship;

  EventConfig(String name, Integer capacity, LocalDate dateFrom, LocalDate dateTo, ZonedDateTime registrationStart, boolean active, boolean archived, String description,
   Boolean hosting, Boolean volunteering, Boolean scholarship
  ) {
    this.name = name;
    this.capacity = capacity;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.registrationStart = registrationStart;
    this.active = active;
    this.archived = archived;
    this.description = description;
    this.hosting = hosting;
    this.volunteering = volunteering;
    this.scholarship = scholarship;
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
            eventConfig.hosting,
            eventConfig.volunteering,
            eventConfig.scholarship
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
        event.setScholarship(eventConfig.scholarship);
        event.setVolunteering(eventConfig.volunteering);
        event.setHosting(eventConfig.hosting);
        eventService.save(event);
      }
    }
  }
}
