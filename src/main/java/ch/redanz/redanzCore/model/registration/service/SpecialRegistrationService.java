package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
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

import java.util.*;
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

  public void save(Registration registration, EventSpecial eventSpecial) {
    specialRegistrationRepo.save(
      new SpecialRegistration(
        registration,
        eventSpecial
      )
    );
  }

  public String getReportSpecials(Registration registration) {
    Map<String, StringBuilder> merged = new HashMap<>();
    specialRegistrationRepo.findAllByRegistration(registration).forEach(specialRegistration -> {
      List<Map<String, String>> privateClassOutText =
        outTextService.getOutTextMapByKey(specialRegistration.getEventSpecial().getName());
      if (privateClassOutText == null || privateClassOutText.isEmpty()) return;
      Map<String, String> map = privateClassOutText.get(0);

      map.forEach((lang, text) -> {
        merged
          .computeIfAbsent(lang, k -> new StringBuilder())
          .append(merged.get(lang).length() == 0 ? "" : ", ")
          .append(text);
      });
    });
    return merged.isEmpty()? "" : merged.toString();
  }

  public Set<Registration> getRegistrationsByEvent(Event event) {
    List<SpecialRegistration> foodRegistrations = specialRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);

    Set<Registration> registrations = foodRegistrations.stream()
      .map(SpecialRegistration::getRegistration)
      .filter(Registration::getActive)
      .collect(Collectors.toSet());

    return registrations;
  }

  public void save(Registration registration, EventPrivateClass eventPrivateClass) {
    privateClassRegistrationRepo.save(
      new PrivateClassRegistration(
        registration,
        eventPrivateClass
      )
    );
  }

  public List<SpecialRegistration> specialRegistrations(Registration registration, JsonObject specialRegistrationRequest) {
    List<SpecialRegistration> specialRegistrations = new ArrayList<>();
    if (specialRegistrationRequest.get("specialRegistrations") != null
      && !specialRegistrationRequest.get("specialRegistrations").isJsonNull()
      && !specialRegistrationRequest.get("specialRegistrations").getAsJsonArray().isEmpty()
    )
    {

      JsonArray specialRequests = specialRegistrationRequest
        .get("specialRegistrations")
        .getAsJsonArray();
      specialRequests.forEach(specialRequest -> {
        if (specialRequest.getAsJsonObject().get("checked") != null
          && specialRequest.getAsJsonObject().get("checked").getAsBoolean()
        ) {
          specialRegistrations.add(
            new SpecialRegistration(
              registration,
              specialService
                .findByEventSpecialId(
                  specialRequest
                    .getAsJsonObject()
                    .get("eventSpecialId")
                    .getAsLong()
                )
            )
          );
        }
      });
    }
    return specialRegistrations;
  }

  public List<PrivateClassRegistration> privateClassRegistration(Registration registration, JsonObject privateClassRegistrationRequest) {
    List<PrivateClassRegistration> privateClassRegistrations = new ArrayList<>();
    if (privateClassRegistrationRequest.get("privateClassRegistrations") != null
      && !privateClassRegistrationRequest.get("privateClassRegistrations").isJsonNull()
      && privateClassRegistrationRequest.get("privateClassRegistrations").isJsonArray()
      && !privateClassRegistrationRequest.get("privateClassRegistrations").getAsJsonArray().isEmpty()) {
      JsonArray privateClassRequests = privateClassRegistrationRequest
        .get("privateClassRegistrations")
        .getAsJsonArray();

      privateClassRequests.forEach(privateClassRequest -> {
        privateClassRegistrations.add(
          new PrivateClassRegistration(
            registration,
            privateClassService
              .findById(
                privateClassRequest
                 .getAsJsonObject()
                 .get("eventPrivateClassId").getAsLong())
          )
        );
      });
    }
    return privateClassRegistrations;
  }
  public Boolean hasRegistrations(EventSpecial eventSpecial, Boolean active) {
    return specialRegistrationRepo.existsByEventSpecialAndRegistrationActive(eventSpecial, active);
  }

  private boolean hasSpecialRegistration(List<SpecialRegistration> specialRegistrations, SpecialRegistration specialRegistration) {
    return specialRegistrations.contains(specialRegistration);
  }

  private boolean hasPrivateClassRegistration(List<PrivateClassRegistration> privateClassRegistrations, EventPrivateClass eventPrivateClass) {
    AtomicBoolean hasPrivateClassRegistration = new AtomicBoolean(false);
    privateClassRegistrations.forEach(privateClassRegistration -> {
      if (privateClassRegistration.getEventPrivateClass() == eventPrivateClass) {
        hasPrivateClassRegistration.set(true);
      }
    });
    return hasPrivateClassRegistration.get();
  }


  public int countEventSpecialsDone(EventSpecial eventSpecial, Event event) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEvent(eventSpecial, workflowStatusService.getDone(), event);
  }
  public int countEventSpecialsSubmitted(EventSpecial eventSpecial, Event event) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEvent(eventSpecial, workflowStatusService.getSubmitted(), event);
  }
  public int countEventSpecialsConfirming(EventSpecial eventSpecial, Event event) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEvent(eventSpecial, workflowStatusService.getConfirming(), event);
  }
  public int countEventSpecialsDone(EventSpecial eventSpecial, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(eventSpecial, workflowStatusService.getDone(), event, danceRole);
  }
  public int countEventSpecialsSubmitted(EventSpecial eventSpecial, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(eventSpecial, workflowStatusService.getSubmitted(), event, danceRole);
  }
  public int countEventSpecialsConfirming(EventSpecial eventSpecial, Event event, DanceRole danceRole) {
    return specialRegistrationRepo.countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(eventSpecial, workflowStatusService.getConfirming(), event, danceRole);
  }

  public int countPrivatesDone(EventPrivateClass eventPrivateClass) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getDone());
  }
  public int countPrivatesSubmitted(EventPrivateClass eventPrivateClass) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getSubmitted());
  }
  public int countPrivatesConfirming(EventPrivateClass eventPrivateClass) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getConfirming());
  }
  public int countPrivatesDone(EventPrivateClass eventPrivateClass, DanceRole danceRole) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatusAndRegistrationDanceRole(eventPrivateClass, workflowStatusService.getDone(), danceRole);
  }
  public int countPrivatesSubmitted(EventPrivateClass eventPrivateClass, DanceRole danceRole) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatusAndRegistrationDanceRole(eventPrivateClass, workflowStatusService.getSubmitted(), danceRole);
  }
  public int countPrivatesConfirming(EventPrivateClass eventPrivateClass, DanceRole danceRole) {
    return privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatusAndRegistrationDanceRole(eventPrivateClass, workflowStatusService.getConfirming(), danceRole);
  }
  public int countEventSpecialRegistrations(EventSpecial eventSpecial, Event event) {
    return
         countEventSpecialsSubmitted(eventSpecial, event)
       + countEventSpecialsConfirming(eventSpecial, event)
       + countEventSpecialsDone(eventSpecial, event);
  }
  public int countEventSpecialRegistrations(EventSpecial eventSpecial, Event event, DanceRole danceRole) {
    return
         countEventSpecialsSubmitted(eventSpecial, event, danceRole)
       + countEventSpecialsConfirming(eventSpecial, event, danceRole)
       + countEventSpecialsDone(eventSpecial, event, danceRole);
  }
  public int countEventSpecialsConfirmingAndDone(EventSpecial eventSpecial, Event event) {
    return
       + countEventSpecialsConfirming(eventSpecial, event)
       + countEventSpecialsDone(eventSpecial, event);
  }

  public int countPrivateClassRegistrations(EventPrivateClass eventPrivateClass) {
    return
         privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getSubmitted())
       + privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getConfirming())
       + privateClassRegistrationRepo.countAllByEventPrivateClassAndRegistration_WorkflowStatus(eventPrivateClass, workflowStatusService.getDone());
  }

  public int countPrivateClassRegistrations(EventPrivateClass eventPrivateClass, DanceRole danceRole) {
    return
      countPrivatesSubmitted(eventPrivateClass, danceRole)
        + countPrivatesConfirming(eventPrivateClass, danceRole)
        + countPrivatesDone(eventPrivateClass, danceRole);
  }

//  public void soldOut(PrivateClass privateClass, boolean soldOut) {
//    privateClass.setSoldOut(soldOut);
//    privateClassService.save(privateClass);
//  }
//  public void soldOut(Special special, boolean soldOut) {
//    special.setSoldOut(soldOut);
//    specialService.save(special);
//  }

  public void updateSpecialRegistrationRequest(Registration registration, JsonObject request) {
    List<SpecialRegistration> requestSpecialRegistrations = specialRegistrations(registration, request);
    List<SpecialRegistration> specialRegistrations = specialRegistrationRepo.findAllByRegistration(registration);

    // delete in current if not in request
    specialRegistrations.forEach(specialRegistration -> {
      if (!hasSpecialRegistration(requestSpecialRegistrations, specialRegistration)){
        specialRegistrationRepo.deleteAllByRegistrationAndEventSpecial(registration, specialRegistration.getEventSpecial());
      }
    });

    // add new from request
    requestSpecialRegistrations.forEach(specialRegistration -> {
      if (!hasSpecialRegistration(specialRegistrations, specialRegistration)){
        save(registration, specialRegistration.getEventSpecial());
      }
    });
  }

  public void updatePrivateClassRequest(Registration registration, JsonObject request) {
    List<PrivateClassRegistration> requestPrivateClassRegistrations = privateClassRegistration(registration, request);
    List<PrivateClassRegistration> privateClassRegistrations = privateClassRegistrationRepo.findAllByRegistration(registration);

    // delete in current if not in request
    privateClassRegistrations.forEach(privateClassRegistration -> {
      if (!hasPrivateClassRegistration(requestPrivateClassRegistrations, privateClassRegistration.getEventPrivateClass())){
        privateClassRegistrationRepo.deleteAllByRegistrationAndEventPrivateClass(registration, privateClassRegistration.getEventPrivateClass());
      }
    });

    // add new from request
    requestPrivateClassRegistrations.forEach(requestPrivateClassRegistration -> {
      if (!hasPrivateClassRegistration(privateClassRegistrations, requestPrivateClassRegistration.getEventPrivateClass())){
        save(registration, requestPrivateClassRegistration.getEventPrivateClass());
      }
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

  public List<String> countEventSpecialRegistrationsAndSplitRoles(EventSpecial eventSpecial, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countEventSpecialRegistrations(eventSpecial, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countEventSpecialRegistrations(eventSpecial, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countPrivateRegistrationsAndSplitRoles(EventPrivateClass eventPrivateClass) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countPrivateClassRegistrations(eventPrivateClass)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countPrivateClassRegistrations(eventPrivateClass, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countEventSpecialsDoneAndSplitRoles(EventSpecial eventSpecial, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countEventSpecialsDone(eventSpecial, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countEventSpecialsDone(eventSpecial, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countPrivatesDoneAndSplitRoles(EventPrivateClass eventPrivateClass) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countPrivatesDone(eventPrivateClass)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countPrivatesDone(eventPrivateClass, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countEventSpecialsSubmittedAndSplitRoles(EventSpecial eventSpecial, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countEventSpecialsSubmitted(eventSpecial, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countEventSpecialsSubmitted(eventSpecial, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countPrivatesSubmittedAndSplitRoles(EventPrivateClass eventPrivateClass) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countPrivatesSubmitted(eventPrivateClass)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countPrivatesSubmitted(eventPrivateClass, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countEventSpecialsConfirmingAndSplitRoles(EventSpecial eventSpecial, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countEventSpecialsConfirming(eventSpecial, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countEventSpecialsConfirming(eventSpecial, event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countPrivatesConfirmingAndSplitRoles(EventPrivateClass eventPrivateClass) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countPrivatesConfirming(eventPrivateClass)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countPrivatesConfirming(eventPrivateClass, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
}
