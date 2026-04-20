package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.HostingService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventCalendarBookItemRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventCalendarRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class CalendarService {
  private final EventCalendarRepo eventCalendarRepo;
  private final EventCalendarBookItemRepo eventCalendarBookItemRepo;
  private final OutTextService outTextService;
  private final BundleService bundleService;
  private final TrackService trackService;
  private final PrivateClassService privateClassService;
  private final SpecialService specialService;
  private final VolunteerService volunteerService;
  private final PersonService personService;
  private final HostingService hostingService;
  private final FoodService foodService;
  private final FoodRegistrationService foodRegistrationService;

  public void save(EventCalendar eventCalendar) {
    eventCalendarRepo.save(eventCalendar);
  }

  public void save(EventCalendarBookItem eventCalendarBookItem) {
    eventCalendarBookItemRepo.save(eventCalendarBookItem);
  }

  public void delete(EventCalendar eventCalendar) {
    eventCalendarRepo.delete(eventCalendar);
  }

  public void delete(EventCalendarBookItem eventCalendarBookItem) {
    eventCalendarBookItemRepo.delete(eventCalendarBookItem);
  }

  public EventCalendar findByTitle(String title) {
    return eventCalendarRepo.findByTitle(title);
  }

  public Boolean existsByEventAndBundle(Event event, Bundle bundle) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, bundle.getBundleId(), BookItemType.BUNDLE);
  }

  public List<EventCalendarBookItem> findByEventAndBundle(Event event, Bundle bundle) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, bundle.getBundleId(), BookItemType.BUNDLE);
  }

  public List<EventCalendarBookItem> findByEventCalendar(EventCalendar eventCalendar) {
    return eventCalendarBookItemRepo.findByEventCalendar(eventCalendar);
  }

  public Object bookItemName(EventCalendarBookItem eventCalendarBookItem) {
    switch (eventCalendarBookItem.getBookItemType()) {
      case SPECIAL:
        return outTextService.getOutTextMapByKey(specialService.findByEventSpecialId(eventCalendarBookItem.getBookItemId()).getName());
      case BUNDLE:
        return bundleService.findByBundleId(eventCalendarBookItem.getBookItemId()).getName();
      case TRACK:
        return trackService.findByTrackId(eventCalendarBookItem.getBookItemId()).getName();
      case PRIVATE:
        return privateClassService.findById(eventCalendarBookItem.getBookItemId()).getName();
      case FOOD:
        return outTextService.getOutTextMapByKey(foodService.findEventFoodSlotById(eventCalendarBookItem.getBookItemId()).getName());
      case VOLUNTEER:
        return personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getFirstName() + " " + personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getLastName();
      case HOST:
        return personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getFirstName() + " " + personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getLastName();
      case HOSTEE:
        return personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getFirstName() + " " + personService.findByPersonId(eventCalendarBookItem.getBookItemId()).getLastName();
      default:
        return "";
    }
  }

  public List<Map<String, Object>> availableBookItems(Event event) {
    List<Map<String, Object>> bookItems = new ArrayList<>();

    event.getEventBundles().forEach(bundle -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", bundle.getBundle().getBundleId());
      bookItem.put("bookItemType", BookItemType.BUNDLE);
      bookItem.put("name", bundle.getBundle().getName());
      bookItems.add(bookItem);
    });

    event.getEventTracks().forEach(track -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", track.getTrack().getTrackId());
      bookItem.put("bookItemType", BookItemType.TRACK);
      bookItem.put("name", track.getTrack().getName());
      bookItems.add(bookItem);
    });
    event.getEventSpecials().forEach(special -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", special.getEventSpecialId());
      bookItem.put("bookItemType", BookItemType.SPECIAL);
      bookItem.put("name", outTextService.getOutTextMapByKey(special.getName()));
      bookItems.add(bookItem);
    });
    event.getEventPrivates().forEach(eventPrivateClass -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", eventPrivateClass.getEventPrivateClassId());
      bookItem.put("bookItemType", BookItemType.PRIVATE);
      bookItem.put("name", eventPrivateClass.getName());
      bookItems.add(bookItem);
    });

    foodService.getEventFoodSlots(event).forEach(eventFoodSlot -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", eventFoodSlot.getEventFoodSlotId());
      bookItem.put("bookItemType", BookItemType.FOOD);
      bookItem.put("name", outTextService.getOutTextMapByKey(eventFoodSlot.getName()));
      bookItems.add(bookItem);
    });

    volunteerService.getAllByEvent(event).forEach(volunteer -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", volunteer.getRegistration().getParticipant().getPersonId());
      bookItem.put("bookItemType", BookItemType.VOLUNTEER);
      bookItem.put("name", volunteer.getRegistration().getParticipant().getFirstName() + " " + volunteer.getRegistration().getParticipant().getLastName());
      bookItems.add(bookItem);
    });

    hostingService.getAllHostRegistrationsByEvent(event).forEach(hostRegistration -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", hostRegistration.getRegistration().getParticipant().getPersonId());
      bookItem.put("bookItemType", BookItemType.HOST);
      bookItem.put("name", hostRegistration.getRegistration().getParticipant().getFirstName() + " " + hostRegistration.getRegistration().getParticipant().getLastName());
      bookItems.add(bookItem);
    });
    hostingService.getAllHosteeRegistrationsByEvent(event).forEach(hosteeRegistration -> {
      Map<String, Object> bookItem = new HashMap<>();
      bookItem.put("bookItemId", hosteeRegistration.getRegistration().getParticipant().getPersonId());
      bookItem.put("bookItemType", BookItemType.HOSTEE);
      bookItem.put("name", hosteeRegistration.getRegistration().getParticipant().getFirstName() + " " + hosteeRegistration.getRegistration().getParticipant().getLastName());
      bookItems.add(bookItem);
    });

    return bookItems;
  }

  public EventCalendar findById(Long id) {
    return eventCalendarRepo.findById(id).orElse(null);
  }

  public List<Map<String, Object>> findAllByEvent(Event event) {
    List<Map<String, Object>> calendarEventList = new ArrayList<>();
    List<Map<String, Object>> availableBookItems = availableBookItems(event);


    eventCalendarRepo.findAllByEvent(event).forEach(calendarEvent -> {
      Map<String, Object> calendarMap = new HashMap<>();
      calendarMap.put("eventStart", event.getEventFrom());
      calendarMap.put("title", outTextService.getOutTextMapByKey(calendarEvent.getTitle()));
      calendarMap.put("description", outTextService.getOutTextMapByKey(calendarEvent.getDescription()));
      calendarMap.put("id", calendarEvent.getId());
      calendarMap.put("active", calendarEvent.getActive());
      calendarMap.put("startTime", calendarEvent.getStartTime());
      calendarMap.put("endTime", calendarEvent.getEndTime());
      calendarMap.put("venue", outTextService.getOutTextMapByKey(calendarEvent.getVenue()));
      List<Map<String, Object>> bookItems = new ArrayList<>();
      eventCalendarBookItemRepo.findByEventCalendar(calendarEvent).forEach(calendarBookItem -> {
        Map<String, Object> bookItem = new HashMap<>();
        bookItem.put("id", calendarBookItem.getId());
        bookItem.put("bookItemId", calendarBookItem.getBookItemId());
        bookItem.put("bookItemType", calendarBookItem.getBookItemType());
        bookItem.put("name", bookItemName(calendarBookItem));
        bookItems.add(bookItem);
      });
      calendarMap.put("bookItems", bookItems);
      calendarMap.put("availableBookItems", availableBookItems);
      calendarEventList.add(calendarMap);
    });
    return calendarEventList;
  }

  public Boolean existsByEventAndTrack(Event event, Track track) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, track.getTrackId(), BookItemType.TRACK);
  }

  public List<EventCalendarBookItem> findByEventAndTrack(Event event, Track track) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, track.getTrackId(), BookItemType.TRACK);
  }

  public Boolean existsByEventFoodSlot(Event event, EventFoodSlot eventFoodSlot) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, eventFoodSlot.getEventFoodSlotId(), BookItemType.FOOD);
  }

  public List<EventCalendarBookItem> findByEventFoodSlot(Event event, EventFoodSlot eventFoodSlot) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, eventFoodSlot.getEventFoodSlotId(), BookItemType.FOOD);
  }

  public Boolean existsByPrivateClass(Event event, EventPrivateClass eventPrivateClass) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, eventPrivateClass.getEventPrivateClassId(), BookItemType.PRIVATE);
  }
  public List<EventCalendarBookItem> findByPrivateClass(Event event, EventPrivateClass eventPrivateClass) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, eventPrivateClass.getEventPrivateClassId(), BookItemType.PRIVATE);
  }
  public Boolean existsByVolunteer(Event event, Person person) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.VOLUNTEER);
  }
  public List<EventCalendarBookItem> findByVolunteer(Event event, Person person) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.VOLUNTEER);
  }
  public Boolean existsByHost(Event event, Person person) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.HOST);
  }
  public List<EventCalendarBookItem> findByHost(Event event, Person person) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.HOST);
  }
  public Boolean existsByHostee(Event event, Person person) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.HOSTEE);
  }
  public List<EventCalendarBookItem> findByHostee(Event event, Person person) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, person.getPersonId(), BookItemType.HOSTEE);
  }
  public Boolean existsBySpecial(Event event, EventSpecial eventSpecial) {
    return eventCalendarBookItemRepo.existsByEventCalendarEventAndBookItemIdAndBookItemType(event, eventSpecial.getEventSpecialId(), BookItemType.SPECIAL);
  }
  public List<EventCalendarBookItem> findBySpecial(Event event, EventSpecial eventSpecial) {
    return eventCalendarBookItemRepo.findAllByEventCalendarEventAndBookItemIdAndBookItemType(event, eventSpecial.getEventSpecialId(), BookItemType.SPECIAL);
  }
}

