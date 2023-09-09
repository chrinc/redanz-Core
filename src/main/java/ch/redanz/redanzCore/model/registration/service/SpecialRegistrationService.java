package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.SpecialRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialRegistrationService {
  private final SpecialRegistrationRepo specialRegistrationRepo;
  private final SpecialService specialService;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final PrivateClassService privateClassService;
  private final WorkflowStatusService workflowStatusService;
  private final OutTextService outTextService;
  private final DanceRoleService danceRoleService;

  public void save(Registration registration, Special special) {
    specialRegistrationRepo.save(
      new SpecialRegistration(
        registration,
        special
      )
    );
  }

  public String getReportSpecials(Registration registration, Language language) {
    AtomicReference<String> specials = new AtomicReference<>();
    specialRegistrationRepo.findAllByRegistration(registration).forEach(specialsRegistration -> {
      String specialOutText = outTextService.getOutTextByKeyAndLangKey(specialsRegistration.getSpecialId().getName(), language.getLanguageKey()).getOutText();
      if (specials.get() == null)
        specials.set(specialOutText);
      else {
        specials.set(specials.get() + ", " + specialOutText);
      }
    });
    return specials.get() == null ? "" : specials.toString();
  }

  public Set<Registration> getRegistrationsByEvent(Event event) {
    List<SpecialRegistration> foodRegistrations = specialRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);

    Set<Registration> registrations = foodRegistrations.stream()
      .map(SpecialRegistration::getRegistration)
      .filter(Registration::getActive)
      .collect(Collectors.toSet());

    return registrations;
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

    // log.info(specialRegistrationRequest.toString());

    // log.info("inc@specialRegistrations");
    if (specialRegistrationRequest.get("specialRegistrations") != null
      && !specialRegistrationRequest.get("specialRegistrations").isJsonNull()
      && !specialRegistrationRequest.get("specialRegistrations").getAsJsonArray().isEmpty()
    )
    {

      JsonArray specialRequests = specialRegistrationRequest
        .get("specialRegistrations")
        .getAsJsonArray();
      // log.info(specialRegistrationRequest.get("specialRegistrations").toString());
      specialRequests.forEach(specialRequest -> {
        if (specialRequest.getAsJsonObject().get("checked") != null
          && specialRequest.getAsJsonObject().get("checked").getAsBoolean()
        ) {
          specialRegistrations.add(
            new SpecialRegistration(
              registration,
              specialService.findBySpecialId(specialRequest.getAsJsonObject().get("specialId").getAsLong())
            )
          );
        }
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
      if (
        specialRegistration.getSpecialId() == special) {
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


  public int countSpecialsDone(Special special, Event event) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEvent(special, workflowStatusService.getDone(), event);
  }
  public int countSpecialsSubmitted(Special special, Event event) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEvent(special, workflowStatusService.getSubmitted(), event);
  }
  public int countSpecialsConfirming(Special special, Event event) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEvent(special, workflowStatusService.getConfirming(), event);
  }
  public int countSpecialsDone(Special special, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(special, workflowStatusService.getDone(), event, danceRole);
  }
  public int countSpecialsSubmitted(Special special, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(special, workflowStatusService.getSubmitted(), event, danceRole);
  }
  public int countSpecialsConfirming(Special special, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(special, workflowStatusService.getConfirming(), event, danceRole);
  }
  public int countSpecialRegistrations(Special special, Event event) {
    return
         countSpecialsSubmitted(special, event)
       + countSpecialsConfirming(special, event)
       + countSpecialsDone(special, event);
  }
  public int countSpecialRegistrations(Special special, Event event, DanceRole danceRole) {
    return
         countSpecialsSubmitted(special, event, danceRole)
       + countSpecialsConfirming(special, event, danceRole)
       + countSpecialsDone(special, event, danceRole);
  }
  public int countSpecialsConfirmingAndDone(Special special, Event event) {
    return
       + countSpecialsConfirming(special, event)
       + countSpecialsDone(special, event);
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

    // log.info("requestSpecialRegistrations: " + requestSpecialRegistrations.size());
    // log.info("specialRegistrations: " + specialRegistrations.size());
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

  private String formatCountToString(String formatedCount, int count, String pfx) {
    if (count > 0) {
      formatedCount = formatedCount.isBlank() ? "" : ", ";
      formatedCount = formatedCount + pfx + count;
      return formatedCount;
    }
    return "";
  }

  public List<String> countSpecialRegistrationsAndSplitRoles(Special special, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSpecialRegistrations(special, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSpecialRegistrations(special, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countSpecialsDoneAndSplitRoles(Special special, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSpecialsDone(special, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSpecialsDone(special, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countSpecialsSubmittedAndSplitRoles(Special special, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSpecialsSubmitted(special, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSpecialsSubmitted(special, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
  public List<String> countSpecialsConfirmingAndSplitRoles(Special special, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSpecialsConfirming(special, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSpecialsConfirming(special, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
}
