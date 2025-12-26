package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventConfig {
  REDANZ_EVENT(
  "Redanz Workshop"
  , 350
  , LocalDate.parse("2026-11-01")
  , LocalDate.parse("2026-11-04")
    ,ZonedDateTime.parse("2023-07-29T11:00:00.000+01:00[Europe/Paris]")
  ,true
    ,false
  ,"Weekend Dance Workshop",
    true,
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
  private final Boolean requireTerms;

  public static void setup(EventService eventService, DiscountService discountService) {

    List<Discount> discounts = new ArrayList<>();


    for (EventConfig eventConfig : EventConfig.values()) {
      discounts.clear();

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
            eventConfig.scholarship,
            null,
            eventConfig.requireTerms
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
        event.setRequireTerms(eventConfig.requireTerms);
        eventService.save(event);
      }
    }
  }
}
