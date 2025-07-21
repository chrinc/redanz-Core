package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventSpecialRepo;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialService {
  private final SpecialRepo specialRepo;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;
  private final EventSpecialRepo eventSpecialRepo;


  public void save(Special special) {
    specialRepo.save(special);
  }

  public List<EventSpecial> findByEvent(Event event) {
    return eventSpecialRepo.findAllByEvent(event);
  }

  public List<EventSpecial> findByEventInfoOnly(Event event, Boolean infoOnly) {
    return eventSpecialRepo.findAllByEventAndInfoOnly(event, infoOnly);
  }
  public List<Special> findAll() {
    return specialRepo.findAll();
  }

  public void delete (Special special) {
    outTextService.delete(special.getName());
    outTextService.delete(special.getDescription());
//    outTextService.delete(special.getUrl());
    specialRepo.delete(special);
  }
//  public Set<Special> findByBundle(Bundle bundle) {
//    return specialRepo.findAllByBundle(bundle).orElse(null);
//  }

  public boolean existsByName(String name) {
    return specialRepo.existsByName(name);
  }
//  public Set<Special> findByEventOrBundle(Event event) {
//    Set<Special> allSpecials;
//    allSpecials = findByEvent(event);
//    event.getEventBundles().forEach(eventBundle -> {
//      allSpecials.addAll(findByBundle(eventBundle.getBundle()));
//    });
//    return allSpecials;
//  }
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

  public void update(JsonObject request) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    Special special;
    Long specialId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (specialId == null || specialId == 0) {
      special = new Special();
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

                if (outTextKey != null) {
                  field = getField(key);
                  field.set(special, outTextKey);
                }
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
              field.set(special, request.get(key).isJsonNull() ? 0.0 : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(special, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "date":
              field = getField(key);

              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(special, request.get(key).isJsonNull() ? null : localDate);
              break;

            case "datetime":
              field = getField(key);
              // registrationStart":{"date":"2023-07-29","time":"23:00"}
              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
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
  }

  public boolean isUsed(Special special) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      event.getEventSpecials().forEach(eventSpecial -> {
        if (eventSpecial.getSpecial().equals(special)) {
          isUsed.set(true);
        }
      });
    });
    return isUsed.get();
  }

  public List<Map<String, String>> getSpecialSchema(){
    return Special.schema();
  }

  public List<Map<String, String>> getSpecialData() {
    List<Map<String, String>> specialsData = new ArrayList<>();
    specialRepo.findAll().forEach(special -> {
      // special data
      Map<String, String> specialData = special.dataMap();
      specialsData.add(specialData);
    });
    return specialsData;
  }

  public List<Map<String, String>> getSpecialsMap() {
    return specialRepo.findAll().stream()
      .map(Special::dataMap)
      .collect(Collectors.toList());
  }

  public List<Map<String, String>> getAllSpecials() {
    List<Map<String, String>> specials = new ArrayList<>();

    findAll().forEach(special -> {
      Map<String, String> specialData = special.dataMap();
      specials.add(specialData);
    });
    return specials;
  }

  public List<Map<String, String>> getAllSpecials(Event event) {
    List<Map<String, String>> specials = new ArrayList<>();
    event.getEventSpecials().forEach(eventSpecial -> {
      specials.add(eventSpecial.getSpecial().dataMap());

    });
    return specials;
  }


}
