package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerSlotRegistration;
import ch.redanz.redanzCore.model.registration.repository.VolunteerRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.VolunteerSlotRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class VolunteerService {
  private final VolunteerRegistrationRepo volunteerRegistrationRepo;
  private final VolunteerSlotRegistrationRepo volunteerSlotRegistrationRepo;
  private final SlotService slotService;
  private final PersonService personService;


  public void saveVolunteerRegistration(Registration registration, JsonObject volunteerRegistration) {
    log.info("inc volunteerRegistration: {}", volunteerRegistration);
    String intro = volunteerRegistration.get("intro").isJsonNull() ? null : volunteerRegistration.get("intro").getAsString();
    log.info("inc introJson: {}", intro);
    String mobile = volunteerRegistration.get("mobile").isJsonNull() ? null : volunteerRegistration.get("mobile").getAsString();
    log.info("inc mobileJson: {}", mobile);
    JsonArray slotsJson = volunteerRegistration.get("slots").getAsJsonArray();
    log.info("inc slotsJson: {}", slotsJson);

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
    log.info("volunteerRegistration 1");

    log.info("volunteerRegistration null? : {}", volunteerRegistrations == null);

    if (volunteerRegistration != null) {

      volunteerRegistrations.add(volunteerRegistration);
      log.info("volunteerRegistration 2");
      volunteerRegistrationMap.put(
        "volunteerRegistration"
        , volunteerRegistrations
      );
      log.info("volunteerRegistration 3");

      volunteerRegistrationMap.put(
        "volunteerSlotRegistration"
        , Collections.singletonList(volunteerSlotRegistrationRepo.findAllByVolunteerRegistration(volunteerRegistration))
      );
      log.info("volunteerRegistration 4");

      volunteerRegistrationMap.put(
        "mobile",
        Collections.singletonList(volunteerRegistration.getRegistration().getParticipant().getMobile())
      );
      log.info("volunteerRegistration 5");

      return volunteerRegistrationMap;
    } else {
      return null;
    }
  }
}
