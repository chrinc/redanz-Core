package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerSlotRegistration;
import ch.redanz.redanzCore.model.registration.repository.VolunteerRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.VolunteerSlotRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
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


  public List<VolunteerRegistration> getAll() {
    return volunteerRegistrationRepo.findAll();
  }

  public List<VolunteerRegistration> getAllCurrentEvent() {
    return volunteerRegistrationRepo.findAllByRegistrationEvent(eventService.getCurrentEvent());
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
  public void saveVolunteerRegistration(Registration registration, JsonObject volunteerRegistration) {
    String intro = volunteerRegistration.get("intro").isJsonNull() ? null : volunteerRegistration.get("intro").getAsString();
    String mobile = volunteerRegistration.get("mobile").isJsonNull() ? null : volunteerRegistration.get("mobile").getAsString();
    JsonArray slotsJson = volunteerRegistration.get("slots").getAsJsonArray();

    // volunteer registration
    volunteerRegistrationRepo.save(
      new VolunteerRegistration(
        registration,
        intro
      )
    );

    // slots
    slotsJson.forEach(slot -> {
      volunteerSlotRegistrationRepo.save(
        new VolunteerSlotRegistration(
          volunteerRegistrationRepo.findByRegistration(registration),
          slotService.findBySlotId(slot.getAsJsonObject().get("slotId").getAsLong())
        )
      );
    });

    // add mobile
    Person person = personService.findByPersonId(registration.getParticipant().getPersonId());
    person.setMobile(mobile);
    personService.savePerson(person);
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
}
