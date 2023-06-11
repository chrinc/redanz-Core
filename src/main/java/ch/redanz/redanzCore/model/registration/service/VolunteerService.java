package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.entities.*;
import ch.redanz.redanzCore.model.registration.repository.VolunteerRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.VolunteerSlotRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import ch.redanz.redanzCore.model.workshop.repository.VolunteerTypeRepo;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
  private final LanguageService languageService;
  private final OutTextService outTextService;
  private final EventService eventService;
  private final VolunteerTypeRepo volunteerTypeRepo;


  public List<VolunteerRegistration> getAll() {
    return volunteerRegistrationRepo.findAll();
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
    log.info("inc@updateVolunteerReg ");
    log.info(volunteerRegistration.toString());
    log.info(volunteerRegistration.get("typeId").toString());
    Long typeId = volunteerRegistration.get("typeId").isJsonNull() ? null : volunteerRegistration.get("typeId").getAsLong();
    String intro = volunteerRegistration.get("intro").isJsonNull() ? null : volunteerRegistration.get("intro").getAsString();
    String mobile = volunteerRegistration.get("mobile").isJsonNull() ? null : volunteerRegistration.get("mobile").getAsString();
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
      registration.getParticipant().setMobile(mobile);
      personService.savePerson(registration.getParticipant());
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

    if (volunteerRegistrationObject != null) {

      // update existing
      updateVolunteerRegistration(registration, volunteerRegistrationObject);
      updateVolunteerSlotRegistration(registration, volunteerRegistrationObject);

    } else {

      // delete existing host registration
      if (volunteerRegistrationRepo.findByRegistration(registration) != null) {
        volunteerSlotRegistrationRepo.deleteAllByVolunteerRegistration(volunteerRegistrationRepo.findByRegistration(registration));
        volunteerRegistrationRepo.deleteAllByRegistration(registration);
      }
    }
  }

}
