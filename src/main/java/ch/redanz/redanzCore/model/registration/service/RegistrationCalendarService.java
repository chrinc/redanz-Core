package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendar;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendarBookItem;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.model.property.immutable.ImmutableCalScale;
import net.fortuna.ical4j.model.property.immutable.ImmutableVersion;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class RegistrationCalendarService {
  private final CalendarService calendarService;
  private final OutTextService outTextService;
  private final FoodRegistrationService foodRegistrationService;
  private final EventService eventService;
  private final PrivateClassService privateClassService;
  private final SpecialRegistrationService specialRegistrationService;
  private final VolunteerService volunteerService;
  private final HostingService hostingService;

  private List<VEvent> calendarEvents(Registration registration, String languageKey) {
    List<VEvent> events = new ArrayList<>();
    Set<EventCalendar> eventCalendars = new HashSet<>();

    // Bundles
    if (calendarService.existsByEventAndBundle(registration.getEvent(), registration.getBundle())) {
      List<EventCalendarBookItem> bundleEventCalendarBookItems = calendarService.findByEventAndBundle(registration.getEvent(), registration.getBundle());
      bundleEventCalendarBookItems.forEach(bundleEventCalendarBookItem -> {
        eventCalendars.add(bundleEventCalendarBookItem.getEventCalendar());
      });
    }
    ;

    // Tracks
    if (registration.getTrack() != null) {
      if (calendarService.existsByEventAndTrack(registration.getEvent(), registration.getTrack())) {
        List<EventCalendarBookItem> trackEventCalendarBookItems = calendarService.findByEventAndTrack(registration.getEvent(), registration.getTrack());
        trackEventCalendarBookItems.forEach(trackEventCalendarBookItem -> {
          eventCalendars.add(trackEventCalendarBookItem.getEventCalendar());
        });
      }
    }

    // Food
    foodRegistrationService.getAllByRegistration(registration).forEach(foodRegistration -> {
      if (
        calendarService.existsByEventFoodSlot(
          registration.getEvent(),
          foodRegistration.getEventFoodSlot()
        )) {
        List<EventCalendarBookItem> foodEventCalendarBookItems = calendarService.findByEventFoodSlot(
          registration.getEvent(),
          foodRegistration.getEventFoodSlot());
        foodEventCalendarBookItems.forEach(foodEventCalendarBookItem -> {
          eventCalendars.add(foodEventCalendarBookItem.getEventCalendar());
        });
      }
    });


    // Private
    privateClassService.findAllByRegistrations(registration).forEach(privateClassRegistration -> {
      if (calendarService.existsByPrivateClass(registration.getEvent(), privateClassRegistration.getEventPrivateClass())) {
        List<EventCalendarBookItem> privateClassCalendarBookItems = calendarService.findByPrivateClass(registration.getEvent(), privateClassRegistration.getEventPrivateClass());
        privateClassCalendarBookItems.forEach(privateClassCalendarBookItem -> {
          eventCalendars.add(privateClassCalendarBookItem.getEventCalendar());
        });
      }
    });


    // Special
    specialRegistrationService.findAllByRegistration(registration).forEach(special -> {
      if (calendarService.existsBySpecial(registration.getEvent(), special.getEventSpecial())) {
        List<EventCalendarBookItem> specialCalendarBookItems = calendarService.findBySpecial(registration.getEvent(), special.getEventSpecial());
        specialCalendarBookItems.forEach(specialCalendarBookItem -> {
          eventCalendars.add(specialCalendarBookItem.getEventCalendar());
        });
      }
    });

    // Volunteers
    if (volunteerService.findByRegistration(registration) != null) {
      if (calendarService.existsByVolunteer(registration.getEvent(), registration.getParticipant())) {
        List<EventCalendarBookItem> volunteerCalendarBookItems = calendarService.findByVolunteer(registration.getEvent(), registration.getParticipant());
        volunteerCalendarBookItems.forEach(volunteerCalendarBookItem -> {
          eventCalendars.add(volunteerCalendarBookItem.getEventCalendar());
        });
      }
    }
    ;

    // Hosts
    if (hostingService.hasHostRegistration(registration)) {
      if (calendarService.existsByHost(registration.getEvent(), registration.getParticipant())) {
        List<EventCalendarBookItem> hostCalendarBookItems = calendarService.findByHost(registration.getEvent(), registration.getParticipant());
        hostCalendarBookItems.forEach(hostCalendarBookItem -> {
          eventCalendars.add(hostCalendarBookItem.getEventCalendar());
        });
      }
    }
    ;

    // Hostees
    if (hostingService.hasHosteeRegistration(registration)) {
      if (calendarService.existsByHostee(registration.getEvent(), registration.getParticipant())) {
        List<EventCalendarBookItem> hostCalendarBookItems = calendarService.findByHostee(registration.getEvent(), registration.getParticipant());
        hostCalendarBookItems.forEach(hostCalendarBookItem -> {
          eventCalendars.add(hostCalendarBookItem.getEventCalendar());
        });
      }
    }
    ;

    // Add Events
    eventCalendars.forEach(calendarEvent -> {
      VEvent event = new VEvent(calendarEvent.getStartTime(), calendarEvent.getEndTime(), outTextService.getOutTextByKeyAndLangKey(calendarEvent.getTitle(), languageKey).getOutText());
      event.add(new Description(outTextService.getOutTextByKeyAndLangKey(calendarEvent.getDescription(), languageKey).getOutText()));
      event.add(new Location(outTextService.getOutTextByKeyAndLangKey(calendarEvent.getVenue(), languageKey).getOutText()));
      event.add(new Uid("'" + calendarEvent.getId().toString() + registration.getEvent().getName() + "'"));
      events.add(event);
    });

    return events;
  }

  public byte[] createCalendarFile(
    Registration registration,
    String languageKey
  ) {
    try {
      Calendar calendar = new Calendar();
      calendar.add(new ProdId(registration.getEvent().getEventId().toString()));
      calendar.add(ImmutableVersion.VERSION_2_0);
      calendar.add(ImmutableCalScale.GREGORIAN);

      calendar.add(new XProperty("X-WR-TIMEZONE", "Europe/Zurich"));
      calendar.add(new XProperty("X-WR-CALNAME", registration.getEvent().getEventId().toString()));
      calendar.add(new XProperty("NAME", registration.getEvent().getName()));

      calendarEvents(registration, languageKey).forEach(calendarEvent -> {
        calendar.add(calendarEvent);
      });

      return calendar.toString().getBytes();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to generate ICS calendar", e);
    }
  }
}
