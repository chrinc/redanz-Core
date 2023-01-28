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
    ,ZonedDateTime.parse("2023-08-01T11:00:00.000+01:00[Europe/Paris]")
  ,true
    ,false
  ,"Weekend Dance Workshop"),
  EVENT2022(
    "Stir it! 2022"
    , 320
    , LocalDate.parse("2022-11-18")
    , LocalDate.parse("2022-11-20")
    ,ZonedDateTime.parse("2022-08-01T11:00:00.000+01:00[Europe/Paris]")
    ,false
    ,false
    ,"Weekend Dance Workshop"),
  EVENT2021(
    "Stir it! 2021"
    , 180
    , LocalDate.parse("2021-11-20")
    , LocalDate.parse("2021-11-20")
    ,null
    ,false
    ,true
    ,"Stir it! Party"
    );
  private final String name;
  private final Integer capacity;
  private final LocalDate dateFrom;
  private final LocalDate dateTo;
  private final ZonedDateTime registrationStart;
  private final Boolean active;
  private final Boolean archived;
  private final String description;

  EventConfig(String name, Integer capacity, LocalDate dateFrom, LocalDate dateTo, ZonedDateTime registrationStart, boolean active, boolean archived, String description) {
    this.name = name;
    this.capacity = capacity;
    this.dateFrom = dateFrom;
    this.dateTo = dateTo;
    this.registrationStart = registrationStart;
    this.active = active;
    this.archived = archived;
    this.description = description;
  }

  public static void setup(EventService eventService) {

    for (EventConfig eventConfig : EventConfig.values()) {
      eventService.save(
        new Event(
          eventConfig.getName(),
          eventConfig.getCapacity(),
          eventConfig.getDateFrom(),
          eventConfig.getDateTo(),
          eventConfig.getRegistrationStart(),
          eventConfig.getActive(),
          eventConfig.getArchived(),
          eventConfig.getDescription()
        )
      );
    }
  }
}
