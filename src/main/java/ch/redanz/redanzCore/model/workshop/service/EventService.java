package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;
  private final BundleService bundleService;
  private final OutTextService outTextService;

  public EventBundle findByEventAndBundle(Event event, Bundle bundle) {
    return bundleEventRepo.findByEventAndBundle(event, bundle);
  }
  public void save(Event event) {
    eventRepo.save(event);
  }
  public boolean eventBundleExists(Event event, Bundle bundle) {
    return bundleEventRepo.existsByEventAndBundle(event, bundle);
  }

  public void save(EventBundle eventBundle) {
    bundleEventRepo.save(eventBundle);
  }
  public List<Event> getAllEvents() {
    return eventRepo.findAllByArchived(false);
  }
  public Event getCurrentEvent() {
    return eventRepo.findDistinctFirstByActive(true);
  }
  public Event getById(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }
  public List<Event> getActiveEvents() {
    return eventRepo.findAllByActiveAndArchived(true, false);
  }
  public List<Event> getInactiveEvents() {
    return eventRepo.findAllByActiveAndArchived(false, false);
  }
  public Event findByEventId(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }
  public Event findByName(String name) {
    return eventRepo.findByName(name);
  }
  public List<Event> findAll() {
    return eventRepo.findAll();
  }
  public boolean existsByName(String name) {
    return eventRepo.existsByName(name);
  }
  public List<Map<String, String>> getEventSchema(){
    return Event.schema();
  }
  public List<Map<String, String>> getEventsData(List<Event> eventList){
    List<Map<String, String>> eventData = new ArrayList<>();
    eventList.forEach(event -> {
      eventData.add(event.dataMap());
    });
    return eventData;
  }

  public List<Map<String, String>> getBundleSchema(){
    return Bundle.schema();
  }
  public List<Map<String, String>> getBundlesData(List<Event> eventList){
    List<Map<String, String>> bundlesData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getEventBundles().forEach(eventBundle -> {
        // bundle data
        Map<String, String> bundleData = eventBundle.getBundle().dataMap();

        // add event info
        bundleData.put("eventId", Long.toString(event.getEventId()));

        bundlesData.add(bundleData);
      });
    });
    return bundlesData;
  }

  public Field getField(String key) {
    Field field;
    try {
      field = Event.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public Set<Track> getAllTracksByEvent(Event event){
    List<Bundle> bundles = bundleService.getAllByEvent(event);
    Set<Track> tracks = new HashSet<>();

    bundles.forEach(bundle -> {
      bundle.getBundleTracks().forEach(bundleTrack -> {
        tracks.add(bundleTrack.getTrack());
      });
    });

    return tracks;
  }

  public void updateEvent(JsonObject request) throws IOException, TemplateException {
     DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
     DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
     Event event;

     Long eventId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (eventId == null || eventId == 0) {
      event = new Event();
    } else {
      event = findByEventId(eventId);
    }

    Event.schema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch(type) {
            case "label":
              if (request.get("label") != null && request.get("label").isJsonArray()) {
                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(key).getAsString());
                field =  getField(key);
                field.set(event, outTextKey);
              }

              break;
            case "text":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "date":
              field = getField(key);

              // Assuming request.get(key).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(event, request.get(key).isJsonNull() ? null : localDate);
              break;

            case "datetime":
              field = getField(key);
              // registrationStart":{"date":"2023-07-29","time":"23:00"}
              // Assuming request.get(key).getAsString() retrieves the date string
              String dateTimeDateString = request.get(key).getAsJsonObject().get("date").getAsString().substring(0, 10);
              String dateTimeTimeString = request.get(key).getAsJsonObject().get("time").isJsonNull() ? "12:00" : request.get(key).getAsJsonObject().get("time").getAsString();
              ZoneId zoneId = ZoneId.of("Europe/Zurich");
              LocalDateTime dateTime = LocalDateTime.parse(dateTimeDateString + " " + dateTimeTimeString, dateTimeFormatter);

              // hack hack hack, need fix plus Days
              ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId).plusDays(1);
              field.set(event, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(event, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
            // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );

    save(event);
  }


  public void deleteEvent(JsonObject request) {
     Long eventId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
     Event event = findByEventId(eventId);
     eventRepo.delete(event);
  }

  public void deleteEventBundle(JsonObject request) {
    Long bundleId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    Bundle bundle = bundleService.findByBundleId(bundleId);
    EventBundle eventBundle = findByEventAndBundle(findByEventId(request.get("eventId").getAsLong()), bundle);
    bundleEventRepo.delete(eventBundle);
  }

}
