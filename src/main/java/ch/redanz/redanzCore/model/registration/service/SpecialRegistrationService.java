package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialRegistrationService {
  private final SpecialRegistrationRepo specialRegistrationRepo;
  private final SpecialService specialService;

  public void save(Registration registration, Special special) {
    specialRegistrationRepo.save(
      new SpecialRegistration(
        registration,
        special
      )
    );
  }

  public List<SpecialRegistration> specialRegistrations(Registration registration, JsonObject specialRegistrationRequest) {
    List<SpecialRegistration> specialRegistrations = new ArrayList<>();
    if (specialRegistrationRequest.get("specialRegistration") != null
      && !specialRegistrationRequest.get("specialRegistration").isJsonNull()
      && !specialRegistrationRequest.get("specialRegistration").getAsJsonArray().isEmpty()) {
      JsonArray specialRequests = specialRegistrationRequest
        .get("specialRegistration")
        .getAsJsonArray();

      specialRequests.forEach(specialRequest -> {
        specialRegistrations.add(
          new SpecialRegistration(
            registration,
            specialService.findBySpecialId(specialRequest.getAsJsonObject().get("specialId").getAsLong())
          )
        );
      });
    }
    log.info("special Reg return: " + specialRegistrations);
    return specialRegistrations;
  }

  private boolean hasSpecialRegistration(List<SpecialRegistration> specialRegistrations, Special special) {
    AtomicBoolean hasSpecialRegistration = new AtomicBoolean(false);
    specialRegistrations.forEach(specialRegistration -> {
      if (specialRegistration.getSpecialId() == special) {
        hasSpecialRegistration.set(true);
      }
    });

    return hasSpecialRegistration.get();
  }

  public void updateSpecialRegistrationRequest(Registration registration, JsonObject request) {
    List<SpecialRegistration> requestSpecialRegistrations = specialRegistrations(registration, request);
    log.info("bfr find existing");
    List<SpecialRegistration> specialRegistrations = specialRegistrationRepo.findAllByRegistration(registration);
    log.info("after find existing");

    // delete in current if not in request
    specialRegistrations.forEach(specialRegistration -> {
      if (!hasSpecialRegistration(requestSpecialRegistrations, specialRegistration.getSpecialId())){
        specialRegistrationRepo.deleteAllByRegistrationAndSpecialId(registration, specialRegistration.getSpecialId());
      }
    });

    // add new from request
    requestSpecialRegistrations.forEach(specialRegistration -> {
      if (!hasSpecialRegistration(specialRegistrations, specialRegistration.getSpecialId())){
        save(registration, specialRegistration.getSpecialId());
      }
    });
  }

  public void saveSpecialRegistration(Registration registration, JsonArray specialRegistration){
    specialRegistration.forEach(special -> {
      save(
        registration,
        specialService.findBySpecialId(special.getAsJsonObject().get("specialId").getAsLong())
      );
    });

  }
  public List<SpecialRegistration> findAllByRegistration(Registration registration) {
    return specialRegistrationRepo.findAllByRegistration(registration);
  }
}
