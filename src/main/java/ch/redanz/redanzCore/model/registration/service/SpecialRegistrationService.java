package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

  public void saveSpecialRegistration(Registration registration, JsonArray specialRegistration){
      log.info("inc, specialRegistration: {}", specialRegistration);
//    JsonArray foodRegistration = specialRegistration.get(0).getAsJsonObject().get("food").getAsJsonArray();
    specialRegistration.forEach(special -> {
      log.info("inc, registration: {}", registration.getParticipant().getFirstName());
      log.info("inc, special: {}", specialService.findBySpecialId(special.getAsJsonObject().get("specialId").getAsLong()).getName());
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
