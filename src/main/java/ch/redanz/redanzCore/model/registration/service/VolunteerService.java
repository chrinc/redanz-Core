package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.registration.repository.VolunteerRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.VolunteerSlotRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.VolunteerTypeRepo;
import ch.redanz.redanzCore.model.workshop.service.EventRegistrationService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
@Slf4j
public class VolunteerService {
  private final VolunteerRegistrationRepo volunteerRegistrationRepo;
  private final VolunteerSlotRegistrationRepo volunteerSlotRegistrationRepo;
  private final SlotService slotService;
  private final PersonService personService;
  private final OutTextService outTextService;
  private final VolunteerTypeRepo volunteerTypeRepo;
  private final EventRepo eventRepo;

  public boolean existsByName(String name) {
    return volunteerTypeRepo.existsByName(name);
  }
  public List<VolunteerRegistration> getAll() {
    return volunteerRegistrationRepo.findAll();
  }
  public VolunteerRegistration findByRegistration(Registration registration) {
    return volunteerRegistrationRepo.existsByRegistration(registration) ? volunteerRegistrationRepo.findByRegistration(registration) : null;
  }

  public String volunteerTypeName(VolunteerRegistration volunteerRegistration, Language language) {
    return outTextService.getOutTextByKeyAndLangKey(volunteerRegistration.getType().getName(), language.getLanguageKey()).getOutText();
  }
  public List<VolunteerRegistration> getAllByEvent(Event event) {
    return volunteerRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);
  }

  public void saveVolunteerType(VolunteerType volunteerType) {
    volunteerTypeRepo.save(volunteerType);
  }

  public List<VolunteerType> getAllVolunteerTypes(Event event) {
    return volunteerTypeRepo.findAllByEvent(event);
  }

  public String getSlots(VolunteerRegistration volunteerRegistration, Language language) {
    AtomicReference<String> slots = new AtomicReference<>();
    volunteerSlotRegistrationRepo.findAllByVolunteerRegistration(volunteerRegistration).forEach(slot ->{
      String slotOutText = outTextService.getOutTextByKeyAndLangKey(slot.getSlot().getName(), language.getLanguageKey()).getOutText();
      if (slots.get() == null) {
        slots.set(slotOutText);
      } else {
        slots.set(slots.get() + ", " + slotOutText);
      }
    });
    return slots.get() == null ? "" : slots.toString();
  }

  public VolunteerType findTypeByName(String name) {
    return volunteerTypeRepo.findByName(name);
  }

  public Map<String, List<Object>> getVolunteerRegistration(Registration registration) {
    Map<String, List<Object>> volunteerRegistrationMap = new HashMap<>();

    VolunteerRegistration volunteerRegistration = volunteerRegistrationRepo.findByRegistration(registration);
    List<Object> volunteerRegistrations = new ArrayList<>();

    if (volunteerRegistration != null) {

      volunteerRegistrations.add(volunteerRegistration);
      volunteerRegistrationMap.put(
        "volunteerRegistration"
        , volunteerRegistrations
      );
      volunteerRegistrationMap.put(
        "volunteerSlotRegistration"
        , Collections.singletonList(volunteerSlotRegistrationRepo.findAllByVolunteerRegistration(volunteerRegistration))
      );
      volunteerRegistrationMap.put(
        "mobile",
        Collections.singletonList(volunteerRegistration.getRegistration().getParticipant().getMobile())
      );

      return volunteerRegistrationMap;
    } else {
      return null;
    }
  }

  // Update Volunteer Request
  private boolean hasVolunteerSlotRegistration(List<VolunteerSlotRegistration> volunteerSlotRegistrations, Slot slot) {
    AtomicBoolean hasVolunteerSlotRegistration = new AtomicBoolean(false);
    volunteerSlotRegistrations.forEach(volunteerSlotRegistration -> {
      if (volunteerSlotRegistration.getSlot() == slot) {
        hasVolunteerSlotRegistration.set(true);
      }
    });

    return hasVolunteerSlotRegistration.get();
  }

  public List<VolunteerSlotRegistration> volunteerSlotRegistrations (Registration registration, JsonObject volunteerRegistration) {
    List<VolunteerSlotRegistration> volunteerSlotRegistrations = new ArrayList<>();
    JsonArray slotsJson = volunteerRegistration.get("slots").getAsJsonArray();

    // volunteer slot registration
    if (slotsJson != null) {
      slotsJson.forEach(slot -> {
        volunteerSlotRegistrations.add(
          new VolunteerSlotRegistration(
            volunteerRegistrationRepo.findByRegistration(registration),
            slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
          )
        );
      });
    }
    return volunteerSlotRegistrations;
  }
  public VolunteerType findVolunteerTypeById(Long volunteerTypeId) {
    return volunteerTypeRepo.findByVolunteerTypeId(volunteerTypeId);
  }

  public void updateVolunteerSlotRegistration(Registration registration, JsonObject volunteerRegistration) {
    List<VolunteerSlotRegistration> requestVolunteerSlotRegistrations = volunteerSlotRegistrations(registration, volunteerRegistration);
    VolunteerRegistration existingVolunteerRegistration = volunteerRegistrationRepo.findByRegistration(registration);
    List<VolunteerSlotRegistration> volunteerSlotRegistrations = volunteerSlotRegistrationRepo.findAllByVolunteerRegistration(existingVolunteerRegistration);
      // log.info("volunteerRegistration: " + volunteerRegistration);

    // delete in current if not in request
    volunteerSlotRegistrations.forEach(volunteerSlotRegistration -> {
      // log.info("requestvolunteerSlotRegistration: " + volunteerSlotRegistration);
      if (!hasVolunteerSlotRegistration(requestVolunteerSlotRegistrations, volunteerSlotRegistration.getSlot())){
        volunteerSlotRegistrationRepo.deleteAllByVolunteerRegistrationAndSlot(existingVolunteerRegistration, volunteerSlotRegistration.getSlot());
      }
    });

    // add new from request
    requestVolunteerSlotRegistrations.forEach(volunteerSlotRegistration -> {
      if (!hasVolunteerSlotRegistration(volunteerSlotRegistrations, volunteerSlotRegistration.getSlot())){
        volunteerSlotRegistrationRepo.save(volunteerSlotRegistration);
      }
    });
  }

  public void updateVolunteerRegistration(Registration registration, JsonObject volunteerRegistration) {
    Long typeId = volunteerRegistration.get("typeId").isJsonNull() ? null : volunteerRegistration.get("typeId").getAsLong();
    String intro = volunteerRegistration.get("intro") == null  || volunteerRegistration.get("intro").isJsonNull() ? null : volunteerRegistration.get("intro").getAsString();
    String mobile = volunteerRegistration.get("mobile") == null  || volunteerRegistration.get("mobile").isJsonNull() ? null : volunteerRegistration.get("mobile").getAsString();
    VolunteerRegistration existingVolunteerRegistration = volunteerRegistrationRepo.findByRegistration(registration);

    if (existingVolunteerRegistration != null) {

      // update existing registration
      if (existingVolunteerRegistration.getIntro() != intro) {
        existingVolunteerRegistration.setIntro(intro);
        volunteerRegistrationRepo.save(existingVolunteerRegistration);
      }

      if (existingVolunteerRegistration != null &&
        existingVolunteerRegistration.getType().getVolunteerTypeId() != typeId) {
        existingVolunteerRegistration.setType(findVolunteerTypeById(typeId));
        volunteerRegistrationRepo.save(existingVolunteerRegistration);
      }

      // update mobile
      if (registration.getParticipant().getMobile() != mobile) {
        registration.getParticipant().setMobile(mobile);
        personService.save(registration.getParticipant());
      }

    } else {

      // new volunteer registration
      volunteerRegistrationRepo.save(
        new VolunteerRegistration(
          registration,
          intro,
          findVolunteerTypeById(typeId)
        )
      );

      // add mobile
//      log.info("inc@updateVolunteerReg bfr set mobile: {}", mobile);
      if (mobile != null) {
        registration.getParticipant().setMobile(mobile);
      }
//      log.info("inc@updateVolunteerReg bfr set save person: {}");
      personService.savePerson(registration.getParticipant());
//      log.info("inc@updateVolunteerReg after set save person: {}");
    }
  }

  public JsonObject volunteerRegistrationObject(JsonObject volunteerRegistrationRequest) {
    JsonElement volunteerRegistration = volunteerRegistrationRequest.get("volunteerRegistration");
    if (volunteerRegistration != null && !volunteerRegistration.isJsonNull()) {
      return volunteerRegistration.getAsJsonObject();
    }
    return null;
  }

  public void updateVolunteerRequest(Registration registration, JsonObject request) {
    JsonObject volunteerRegistrationObject = volunteerRegistrationObject(request);

//    log.info("inc@updateVolunteerRequest");
    if (volunteerRegistrationObject != null) {

      // update existing
//      log.info("inc@bfrupdateRegistration");
      updateVolunteerRegistration(registration, volunteerRegistrationObject);
//      log.info("inc@bfrupdateSlotRegistration");
      updateVolunteerSlotRegistration(registration, volunteerRegistrationObject);

    } else {
//      log.info("inc@object is null");

      // delete existing volunteer registration
      if (volunteerRegistrationRepo.findByRegistration(registration) != null) {
        volunteerSlotRegistrationRepo.deleteAllByVolunteerRegistration(volunteerRegistrationRepo.findByRegistration(registration));
        volunteerRegistrationRepo.deleteAllByRegistration(registration);
      }
    }

//    log.info("inc@done");
  }

  public boolean hasVolunteerRegistration(Registration registration) {
    return volunteerRegistrationRepo.existsByRegistration(registration);
  }


  public void onDeleteVolunteer(Registration registration) {
    VolunteerRegistration volunteerRegistration = volunteerRegistrationRepo.findByRegistration(registration);
    volunteerSlotRegistrationRepo.deleteAllByVolunteerRegistration(volunteerRegistration);
    volunteerRegistrationRepo.deleteAllByRegistration(registration);
  }

  public List<Map<String, String>> getVolunteerTypeSchema() {
    return VolunteerType.schema();
  }
  public List<Map<String, String>> getVolunteerTypeData() {
    List<Map<String, String>> volunteerTypesData = new ArrayList<>();
    volunteerTypeRepo.findAll().forEach(volunteerType -> {
      // discount data
      Map<String, String> volunteerTypeData = volunteerType.dataMap();
      volunteerTypesData.add(volunteerTypeData);
    });
    return volunteerTypesData;
  }

  public void delete (VolunteerType volunteerType) {
    outTextService.delete(volunteerType.getName());
    outTextService.delete(volunteerType.getDescription());
    volunteerTypeRepo.delete(volunteerType);
  }

  public boolean isUsed(VolunteerType volunteerType) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if(event.getVolunteerTypes().contains(volunteerType)
      ) {
        isUsed.set(true);
      };
    });
    return isUsed.get();
  }

  public Field getField(String key) {
    Field field;
    try {
      field = VolunteerType.class.getDeclaredField(key);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
    field.setAccessible(true);
    return field;
  }

  public void updateVolunteerType(JsonObject request) throws IOException, TemplateException {
    Long id = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    VolunteerType volunteerType;

    if (id == null || id == 0) {
      volunteerType = new VolunteerType();
    } else {
      volunteerType = findVolunteerTypeById(id);
    }

    VolunteerType.schema().forEach(
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
                  field.set(volunteerType, outTextKey);
                }
              }

              break;
            case "text":
              field = getField(key);
              field.set(volunteerType, request.get(key).isJsonNull() ? "" : request.get(key).getAsString());
              break;

            case "number":
              field = getField(key);
              field.set(volunteerType, request.get(key).isJsonNull() ? null : Integer.parseInt(request.get(key).getAsString()));
              break;

            case "double":
              field = getField(key);
              field.set(volunteerType, request.get(key).isJsonNull() ? null : Double.parseDouble(request.get(key).getAsString()));
              break;

            case "color":
              field = getField(key);
              field.set(volunteerType, request.get(key).isJsonNull() ? null :
                request.get(key).getAsJsonObject().isJsonNull() ? request.get(key).getAsString() :
                  request.get(key).getAsJsonObject().get("hex").getAsString());
              break;

            case "bool":
              field = getField(key);
              field.set(volunteerType, request.get(key).isJsonNull() ? null : Boolean.valueOf(request.get(key).getAsString()));
              break;

            default:
              // Nothing will happen here
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    );
    saveVolunteerType(volunteerType);
  }

}
