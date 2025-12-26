package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.PrivateClassRepo;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PrivateClassService {
  private final PrivateClassRepo privateClassRepo;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final OutTextService outTextService;
  private final EventRepo eventRepo;

  public boolean existsByName(String name) {
    return privateClassRepo.existsByName(name);
  }
  public PrivateClass findByName(String name) {
    return privateClassRepo.findByName(name);
  }
  public void save(PrivateClass privateClass) {
    privateClassRepo.save(privateClass);
  }

  public String getReportPrivates(Registration registration, Language language) {
    AtomicReference<String> privates = new AtomicReference<>();
    privateClassRegistrationRepo.findAllByRegistration(registration).forEach(privateRegistration -> {
      String privateOutText = outTextService.getOutTextByKeyAndLangKey(privateRegistration.getPrivateClass().getDescription(), language.getLanguageKey()).getOutText();
      if (privates.get() == null)
      privates.set(privateOutText);
      else {
        privates.set(privates.get() + ", " + privateOutText);
      }
    });
    return privates.get() == null ? "" : privates.toString();
  }

  public Boolean hasRegistration(PrivateClass privateClass,  Boolean active) {
    return privateClassRegistrationRepo
      .findAllByRegistrationActive(active)
      .stream()
      .anyMatch(pr -> pr.getPrivateClass().equals(privateClass));
  }
  public Boolean hasRegistration(Event event, PrivateClass privateClass,  Boolean active) {
    return privateClassRegistrationRepo
      .findAllByRegistrationEventAndRegistrationActive(event, active)
      .stream()
      .anyMatch(pr -> pr.getPrivateClass().equals(privateClass));
  }

  public List<PrivateClass> findAll() {
    return privateClassRepo.findAll();
  }

  public List<PrivateClassRegistration> findAllByRegistrations(Registration registration) {
    return privateClassRegistrationRepo.findAllByRegistration(registration);
  }

  public PrivateClass findByPrivateClassId(Long privateClassId) {
    return privateClassRepo.findByPrivateClassId(privateClassId);
  }

  public Set<Registration> getRegistrationsByEvent(Event event) {
    List<PrivateClassRegistration> privateRegistrations = privateClassRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);

    Set<Registration> registrations = privateRegistrations.stream()
      .map(PrivateClassRegistration::getRegistration)
      .filter(Registration::getActive)
      .collect(Collectors.toSet());

    return registrations;
  }


  public Field getField(String key) {
    Field field;
    try {
      field = PrivateClass.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void update(JsonObject request) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    PrivateClass privateClass;
    Long privateId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (privateId == null || privateId == 0) {
      privateClass = new PrivateClass();
    } else {
      privateClass = findByPrivateClassId(privateId);
    }

    PrivateClass.schema().forEach(
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
                  field.set(privateClass, outTextKey);
                }
              }
              break;
            case "text":
              field = getField(key);

              field.set(privateClass, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(privateClass, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(privateClass, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(privateClass, request.get(key).isJsonNull() ? null :
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

              field.set(privateClass, request.get(key).isJsonNull() ? null : localDate);
              break;


            case "bool":
              field = getField(key);
              field.set(privateClass, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    save(privateClass);
  }

  public void delete(PrivateClass privateClass) {
    outTextService.delete(privateClass.getDescription());
    privateClassRepo.delete(privateClass);
  }

  public List<Map<String, String>> getPrivateSchema() {
    return PrivateClass.schema();
  }
  public List<Map<String, String>> getPrivateData() {
    List<Map<String, String>> privatesData = new ArrayList<>();
    privateClassRepo.findAll().forEach(privateClass -> {
        // discount data
        Map<String, String> privateData = privateClass.dataMap();
        privatesData.add(privateData);
    });
    return privatesData;
  }

  public List<Map<String, String>> getAllPrivates() {
    List<Map<String, String>> privates = new ArrayList<>();

    findAll().forEach(privateClass -> {
      Map<String, String> privatesData = privateClass.eventDataMap();
      privates.add(privatesData);
    });
    return privates;
  }


  public List<Map<String, String>> getEventPrivateSchema() {
    List<Map<String, String>> eventPrivatesSchema = PrivateClass.eventSchema();
    eventPrivatesSchema.forEach(item -> {
      item.put("list",getAllPrivates().toString());
    });
    return eventPrivatesSchema;
  }

//  public List<Map<String, String>> getEventPrivateData(Event event) {
//    List<Map<String, String>> privatesData = new ArrayList<>();
//    event.getEventPrivates().forEach(eventPrivateClass -> {
//      HashMap<String, String> privateClassData = new HashMap<>();
//      privateClassData.put("id", eventPrivateClass.getEventPrivateClassId().toString());
//      privatesData.add(privateClassData);
//      privatesData.add(privateClass.eventDataMap());
//    });
//    return privatesData;
//  }

  public List<Map<String, String>> getPrivatesMap() {
    return privateClassRepo.findAll().stream()
      .map(PrivateClass::eventDataMap)
      .collect(Collectors.toList());
  }

  public boolean isUsed(PrivateClass privateClass) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      event.getEventPrivates().forEach(eventPrivateClass -> {
        if (eventPrivateClass.getPrivateClass().equals(privateClass)) {
          isUsed.set(true);
        }
      });
    });
    return isUsed.get();
  }

}
