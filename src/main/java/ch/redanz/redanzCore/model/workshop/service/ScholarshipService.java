package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.VolunteerTypeRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class ScholarshipService {
  private final OutTextService outTextService;
  private final EventTypeSlotService eventTypeSlotService;
  private final EventRepo eventRepo;
  private final SlotService slotService;
  private final VolunteerTypeRepo volunteerTypeRepo;

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

  public void updateScholarship(JsonObject request, Event event) throws IOException, TemplateException {
    log.info("inc@updateScholarship, request: {}", request);
    getScholarshipSchema().forEach(
      stringStringMap -> {
        String key = stringStringMap.get("key");
        String type = stringStringMap.get("type");
        Field field;

        try {
          switch(type) {
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

    eventRepo.save(event);
  }

  public void deleteScholarship(JsonObject request, Event event) {
    event.setScholarship(false);
    eventRepo.save(event);
  }

  public List<Map<String, String>> getScholarshipSchema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");               put("type", "id");            put("label", "id");}});
        add(new HashMap<>() {{put("key", "scholarship");      put("type", "bool");          put("labelTrue", "Enable Scholarship"); put("labelFalse", "Disable Scholarship");}});
        add(new HashMap<>() {{put("key", "count");            put("type", "single");}});
      }
    };
  }

  public List<Map<String, String>> getScholarshipData(List<Event> eventList) {
    List<Map<String, String>> ScholarshipData = new ArrayList<>();
    eventList.forEach(event -> {

      // discount data
      Map<String, String> volunteerMap = dataMap(event);
      ScholarshipData.add(volunteerMap);
    });
    return ScholarshipData;
  }

  public Map<String, String> dataMap(Event event) {
    return new HashMap<>() {
      {
        put("id", String.valueOf(event.getEventId()));
        put("scholarship", String.valueOf(event.isScholarship()));
      }
    };
  }
}
