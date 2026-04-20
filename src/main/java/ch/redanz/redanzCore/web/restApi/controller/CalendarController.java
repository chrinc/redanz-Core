package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendar;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendarBookItem;
import ch.redanz.redanzCore.model.workshop.service.CalendarService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
@RestController
@RequestMapping("core-api/app/calendar")
@AllArgsConstructor
public class CalendarController {
  private final EventService eventService;
  private final CalendarService calendarService;
  private final OutTextService outTextService;
  private Long getLong(JsonObject obj, String key) {
    return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsLong() : null;
  }
  private String getString(JsonObject obj, String key) {
    return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsString() : null;
  }
  private Boolean getBoolean(JsonObject obj, String key) {
    return obj.has(key) && !obj.get(key).isJsonNull() ? obj.get(key).getAsBoolean() : null;
  }
  private JsonArray getArray(JsonObject obj, String key) {
    return obj.has(key) && !obj.get(key).isJsonNull() ? obj.getAsJsonArray(key) : null;
  }
  private JsonObject getFirstObjectFromArray(JsonObject obj, String key) {
    JsonArray array = getArray(obj, key);
    return array != null && !array.isEmpty() ? array.get(0).getAsJsonObject() : null;
  }
  private ZonedDateTime getZonedDateTime(JsonObject obj, String key) {
    String value = getString(obj, key);
    return value != null
      ? LocalDateTime.parse(value).atZone(ZoneId.systemDefault())
      : null;
  }
  private String upsertLabel(JsonObject labelObject, String key) {
    if (labelObject == null) {
      return key;
    }

    if (key == null || key.isEmpty()) {
      return outTextService.newLabel(labelObject);
    }
    outTextService.updateLabel(labelObject, key);
    return key;
  }
  private void syncBookItems(EventCalendar eventCalendar, JsonArray bookItems) {
    Set<Long> existingRelationIdsFromRequest = new HashSet<>();
    if (bookItems != null) {
      for (JsonElement element : bookItems) {
        JsonObject bookItem = element.getAsJsonObject();
        Long id = getLong(bookItem, "id");
        if (id != null) {
          existingRelationIdsFromRequest.add(id);
        }
      }
    }

    calendarService.findByEventCalendar(eventCalendar).forEach(eventCalendarBookItem -> {
      if (!existingRelationIdsFromRequest.contains(eventCalendarBookItem.getId())) {
        calendarService.delete(eventCalendarBookItem);
      }
    });

    if (bookItems != null) {
      for (JsonElement element : bookItems) {
        JsonObject bookItem = element.getAsJsonObject();
        Long id = getLong(bookItem, "id");
        Long bookItemId = getLong(bookItem, "bookItemId");
        String bookItemType = getString(bookItem, "bookItemType");

        if (id == null && bookItemId != null && bookItemType != null) {
          calendarService.save(
            new EventCalendarBookItem(
              eventCalendar,
              bookItemId,
              BookItemType.valueOf(bookItemType)
            )
          );
        }
      }
    }
  }

  @GetMapping(path = "/all")
  public List<Map<String, Object>> all(
    @RequestParam("userId") Long userId, // used for security
    @RequestParam("eventId") Long eventId
  ) {
    return calendarService.findAllByEvent(eventService.findByEventId(eventId));
  }

  @PostMapping(path = "/upsert")
  public void upsert(
    @RequestParam("userId") Long userId, // used for security
    @RequestParam("eventId") Long eventId,
    @RequestBody String jsonObject
  ) {
    JsonObject request = JsonParser.parseString(jsonObject).getAsJsonObject();
    Long eventCalendarId = getLong(request, "id");
    EventCalendar eventCalendar = eventCalendarId != null
      ? calendarService.findById(eventCalendarId)
      : new EventCalendar();

    eventCalendar.setTitle(
      upsertLabel(getFirstObjectFromArray(request, "title"), eventCalendar.getTitle())
    );
    eventCalendar.setDescription(
      upsertLabel(getFirstObjectFromArray(request, "description"), eventCalendar.getDescription())
    );
    eventCalendar.setVenue(
      upsertLabel(getFirstObjectFromArray(request, "venue"), eventCalendar.getVenue())
    );
    eventCalendar.setEvent(eventService.findByEventId(eventId));
    eventCalendar.setStartTime(getZonedDateTime(request, "start"));
    eventCalendar.setEndTime(getZonedDateTime(request, "end"));
    Boolean active = getBoolean(request, "active");
    eventCalendar.setActive(Boolean.TRUE.equals(active));

    calendarService.save(eventCalendar);
    syncBookItems(eventCalendar, getArray(request, "bookItems"));
  }

  @PostMapping(path = "/delete")
  public void delete(
    @RequestParam("userId") Long userId, // used for security
    @RequestBody Long eventCalendarId
  ) {
    calendarService.delete(calendarService.findById(eventCalendarId));
  }
}
