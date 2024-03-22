package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  public List<PrivateClass> findByEvent(Event event) {
    return privateClassRepo.findAllByEvent(event).isPresent() ?
      privateClassRepo.findAllByEvent(event).get() :
      null;
  }

  public List<PrivateClass> findByEventAndBundle(Event event, Bundle bundle) {
    if (bundle.getSimpleTicket()) {return new ArrayList<>();}
    else { return findByEvent(event);}
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

  public void updatePrivates(JsonObject request, Event event) throws IOException, TemplateException {
    log.info("inc@updatePrivates, request: {}", request);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    PrivateClass privateClass;
    boolean isNew = false;
    Long privateId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();

    if (privateId == null || privateId == 0) {
      privateClass = new PrivateClass();
      isNew = true;
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
                field =  getField(key);
                field.set(privateClass, outTextKey);
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

              // Assuming request.get(key).getAsString() retrieves the date string
              String dateString = request.get(key).getAsString();

              // Parse the string into a LocalDate object
              // hack hack hack, need fix
              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);

              field.set(privateClass, request.get(key).isJsonNull() ? null : localDate);
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
              field.set(privateClass, request.get(key).isJsonNull() ? null : zonedDateTime);
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
    if (isNew) {
      List<PrivateClass> newPrivateClassList = event.getPrivateClasses();
      newPrivateClassList.add(privateClass);
      event.setPrivateClasses(newPrivateClassList);
      eventRepo.save(event);
    }
  }

  public void deletePrivates(JsonObject request, Event event) {
    Long privateId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    List<PrivateClass> privateList = event.getPrivateClasses();
    privateList.remove(findByPrivateClassId(privateId));
    eventRepo.save(event);
    privateClassRepo.deleteById(privateId);
  }

  public List<Map<String, String>> getPrivateSchema() {
    return PrivateClass.schema();
  }
  public List<Map<String, String>> getPrivateData(List<Event> eventList) {
    List<Map<String, String>> privatesData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getPrivateClasses().forEach(privateClass -> {
        // discount data
        Map<String, String> privateData = privateClass.dataMap();

        // add event info
        privateData.put("eventId", Long.toString(event.getEventId()));

        privatesData.add(privateData);
      });
    });

    return privatesData;
  }

}
