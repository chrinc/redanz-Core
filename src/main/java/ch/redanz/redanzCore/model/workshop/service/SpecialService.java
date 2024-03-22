package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.SpecialRepo;
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
public class SpecialService {
  private final SpecialRepo specialRepo;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;
  public void save(Special special) {
    specialRepo.save(special);
  }

  public Set<Special> findByEvent(Event event) {
    return specialRepo.findAllByEvent(event).orElse(null);
  }
  public Set<Special> findByBundle(Bundle bundle) {
    return specialRepo.findAllByBundle(bundle).orElse(null);
  }

  public boolean existsByName(String name) {
    return specialRepo.existsByName(name);
  }
  public Set<Special> findByEventOrBundle(Event event) {
    Set<Special> allSpecials;
    allSpecials = findByEvent(event);
    event.getEventBundles().forEach(eventBundle -> {
      allSpecials.addAll(findByBundle(eventBundle.getBundle()));
    });
    return allSpecials;
  }
  public Special findByName(String name) {
    return specialRepo.findByName(name);
  }
  public Special findBySpecialId(Long foodId) {
    return specialRepo.findBySpecialId(foodId);
  }

  public Field getField(String key) {
    Field field;
    try {
      field = Special.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateSpecial(JsonObject request, Event event) throws IOException, TemplateException {
    log.info("inc@updateSpecial, request: {}", request);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Special special;
    boolean isNew = false;

    Long specialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (specialId == null || specialId == 0) {
      special = new Special();
      isNew = true;
    } else {
      special = findBySpecialId(specialId);
    }

    Special.schema().forEach(
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
                field.set(special, outTextKey);
              }
              break;
            case "text":
              field = getField(key);

              field.set(special, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(special, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(special, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(special, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "date":
              field = getField(key);

              // Assuming request.get(key).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(special, request.get(key).isJsonNull() ? null : localDate);
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
              field.set(special, request.get(key).isJsonNull() ? null : zonedDateTime);
              break;

            case "bool":
              field = getField(key);
              field.set(special, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(special);
    if (isNew) {
      List<Special> newSpecialsList = event.getSpecials();
      newSpecialsList.add(special);
      event.setSpecials(newSpecialsList);
      eventRepo.save(event);
    }
  }

  public void deleteSpecial(JsonObject request, Event event) {
    Long specialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    List<Special> specialList = event.getSpecials();
    specialList.remove(findBySpecialId(specialId));
    eventRepo.save(event);
    specialRepo.deleteById(specialId);
  }

  public List<Map<String, String>> getSpecialSchema(){
    return Special.schema();
  }
  public List<Map<String, String>> getSpecialData(List<Event> eventList){
    List<Map<String, String>> specialsData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getSpecials().forEach(special -> {
        // discount data
        Map<String, String> specialData = special.dataMap();

        // add event info
        specialData.put("eventId", Long.toString(event.getEventId()));

        specialsData.add(specialData);
      });
    });

    return specialsData;
  }
}
