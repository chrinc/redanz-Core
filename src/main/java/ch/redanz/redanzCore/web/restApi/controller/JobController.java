package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/app/jobs")
public class JobController {
  private final EventService eventService;
  private final CheckInService checkInService;
  private final RegistrationService registrationService;
  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationReleaseService registrationReleaseService;
  private final RegistrationReminderService registrationReminderService;
  private final RegistrationCancelService registrationCancelService;
  private final ErrorLogService errorLogService;
  private final PersonService personService;
  private final UserService userService;
  private final WorkflowStatusService workflowStatusService;

  @GetMapping(path = "/run-cancel")
  public void runCancel(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      registrationCancelService.doCancel(eventService.findByEventId(eventId));
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

//  @GetMapping(path = "/run-matching")
//  @Transactional
//  public void runMatching(
//    @RequestParam("eventId") Long eventId
//  ) {
//    try {
//      Event event = eventService.findByEventId(eventId);
//      registrationService.getAllSubmittedRegistrations(event).forEach(registration -> {
//        registrationMatchingService.checkIsRelease(registration);
//        registrationReleaseService.doRelease(registration);
//        registrationService.updateSoldOut(event);
//      });
//    } catch (ApiRequestException apiRequestException) {
//      throw new ApiRequestException(apiRequestException.getMessage());
//    } catch (Exception exception) {
//
//      errorLogService.addLog("RUN-MATCHING", exception.toString());
//      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
//    }
//  }

  @GetMapping(path = "/run-release")
  public void runRelease(@RequestParam("eventId") Long eventId) {
    try {
      Event event = eventService.findByEventId(eventId);

      boolean restart;
      int maxRuns = 500;
      int runCount = 0;

      do {
        restart = false;
        runCount++;
        registrationService.updateSoldOut(event);

        if (runCount > maxRuns) {
          throw new IllegalStateException("runRelease exceeded maxRuns");
        }
        List<Registration> registrations =
          registrationService.findAllByEventAndStatus(
            event,
            workflowStatusService.getSubmitted()
          );

        for (Registration registration : registrations) {
          registrationMatchingService.checkIsRelease(registration);

          if (registrationReleaseService.doRelease(registration)) {
            registrationService.updateSoldOut(event);
            restart = true;
            break;
          }
        }

      } while (restart);

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      errorLogService.addLog("RUN-RELEASE", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }


  @GetMapping(path = "/run-reminder")
  public void runReminder(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      registrationReminderService.doRemind(eventService.findByEventId(eventId));
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping (path = "/checkIn/create")
  @Transactional
  public void createCheckIn(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      checkInService.resetByEvent(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping (path = "/checkIn/update")
  @Transactional
  public void updateCheckIn(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      checkInService.updateByEvent(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }

  @GetMapping (path = "/change-role")
  @Transactional
  public void createCheckIn(
    @RequestParam("personId") Long personId,
    @RequestParam("role") String userRoleName
  ) {
    try {
      Person person = personService.findByPersonId(personId);
      UserRole userRole = userService.findUserRoleByName(userRoleName);
      userService.changeUserRole(person.getUser(), userRole);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE.getOutTextKey());
    }
  }
}
