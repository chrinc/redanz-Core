package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
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
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final PrivateClassService privateClassService;
  private final WorkflowStatusService workflowStatusService;

  public void save(Registration registration, Special special) {
    specialRegistrationRepo.save(
      new SpecialRegistration(
        registration,
        special
      )
    );
  }

  public void save(Registration registration, PrivateClass privateClass) {
    privateClassRegistrationRepo.save(
      new PrivateClassRegistration(
        registration,
        privateClass
      )
    );
  }

  public List<SpecialRegistration> specialRegistrations(Registration registration, JsonObject specialRegistrationRequest) {
    List<SpecialRegistration> specialRegistrations = new ArrayList<>();

    // log.info("inc@specialRegistrations");
    if (specialRegistrationRequest.get("specialRegistrations") != null
      && !specialRegistrationRequest.get("specialRegistrations").isJsonNull()
      && !specialRegistrationRequest.get("specialRegistrations").getAsJsonArray().isEmpty()) {
      JsonArray specialRequests = specialRegistrationRequest
        .get("specialRegistrations")
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
    // log.info("special Reg return: " + specialRegistrations);
    return specialRegistrations;
  }

  public List<PrivateClassRegistration> privateClassRegistration(Registration registration, JsonObject privateClassRegistrationRequest) {
    // log.info("inc@privateClassRegistration");
    List<PrivateClassRegistration> privateClassRegistrations = new ArrayList<>();
    if (privateClassRegistrationRequest.get("privateClassRegistrations") != null
      && !privateClassRegistrationRequest.get("privateClassRegistrations").isJsonNull()
      && privateClassRegistrationRequest.get("privateClassRegistrations").isJsonArray()
      && !privateClassRegistrationRequest.get("privateClassRegistrations").getAsJsonArray().isEmpty()) {
      JsonArray privateClassRequests = privateClassRegistrationRequest
        .get("privateClassRegistrations")
        .getAsJsonArray();

      // log.info("inc@bfr privateClassRequests:");
      // log.info("inc@bfr privateClassRequests:" + privateClassRequests);
      privateClassRequests.forEach(privateClassRequest -> {
        // log.info("inc@bfr privateClassId:" + privateClassRequest.getAsJsonObject().get("privateClassId").getAsLong());
        privateClassRegistrations.add(
          new PrivateClassRegistration(
            registration,
            privateClassService.findByPrivateClassId(privateClassRequest.getAsJsonObject().get("privateClassId").getAsLong())
          )
        );
      });
    }
    // log.info("inc@bfr return");
    return privateClassRegistrations;
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

  private boolean hasPrivateClassRegistration(List<PrivateClassRegistration> privateClassRegistrations, PrivateClass privateClass) {
    AtomicBoolean hasPrivateClassRegistration = new AtomicBoolean(false);
    privateClassRegistrations.forEach(privateClassRegistration -> {
      if (privateClassRegistration.getPrivateClass() == privateClass) {
        hasPrivateClassRegistration.set(true);
      }
    });
    return hasPrivateClassRegistration.get();
  }

  public int countSpecialRegistrations(Special special) {
    return
         specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatus(special, workflowStatusService.getSubmitted())
       + specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatus(special, workflowStatusService.getConfirming())
       + specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatus(special, workflowStatusService.getDone());
  }

  public int countPrivateClassRegistrations(PrivateClass privateClass) {
    return
         privateClassRegistrationRepo.countAllByPrivateClassAndRegistration_WorkflowStatus(privateClass, workflowStatusService.getSubmitted())
       + privateClassRegistrationRepo.countAllByPrivateClassAndRegistration_WorkflowStatus(privateClass, workflowStatusService.getConfirming())
       + privateClassRegistrationRepo.countAllByPrivateClassAndRegistration_WorkflowStatus(privateClass, workflowStatusService.getDone());
  }

  public void soldOut(PrivateClass privateClass, boolean soldOut) {
    privateClass.setSoldOut(soldOut);
    privateClassService.save(privateClass);
  }
  public void soldOut(Special special, boolean soldOut) {
    special.setSoldOut(soldOut);
    specialService.save(special);
  }

  public void updateSpecialRegistrationRequest(Registration registration, JsonObject request) {
    List<SpecialRegistration> requestSpecialRegistrations = specialRegistrations(registration, request);
    List<SpecialRegistration> specialRegistrations = specialRegistrationRepo.findAllByRegistration(registration);

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

  public void updatePrivateClassRequest(Registration registration, JsonObject request) {
    List<PrivateClassRegistration> requestPrivateClassRegistrations = privateClassRegistration(registration, request);
    List<PrivateClassRegistration> privateClassRegistrations = privateClassRegistrationRepo.findAllByRegistration(registration);
    // log.info(String.valueOf(privateClassRegistrations.size()));

    // delete in current if not in request
    privateClassRegistrations.forEach(privateClassRegistration -> {
      if (!hasPrivateClassRegistration(requestPrivateClassRegistrations, privateClassRegistration.getPrivateClass())){
        privateClassRegistrationRepo.deleteAllByRegistrationAndPrivateClass(registration, privateClassRegistration.getPrivateClass());
      }
    });

    // add new from request
    requestPrivateClassRegistrations.forEach(requestPrivateClassRegistration -> {
      if (!hasPrivateClassRegistration(privateClassRegistrations, requestPrivateClassRegistration.getPrivateClass())){
        save(registration, requestPrivateClassRegistration.getPrivateClass());
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

  public List<PrivateClassRegistration> findAllPrivateClassesByRegistration(Registration registration) {
    return privateClassRegistrationRepo.findAllByRegistration(registration);
  }
}
