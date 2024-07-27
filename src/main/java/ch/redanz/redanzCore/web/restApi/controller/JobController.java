package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserRegistrationService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.jobs.EODCancelJob;
import ch.redanz.redanzCore.model.registration.jobs.EODMatchingJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReleaseJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReminderJob;
import ch.redanz.redanzCore.model.registration.service.CheckInService;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
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

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/app/jobs")
public class JobController {

  private final EODCancelJob eodCancelJob;
  private final EODMatchingJob eodMatchingJob;
  private final EODReleaseJob eodReleaseJob;
  private final EODReminderJob eodReminderJob;
  private final EventService eventService;
  private final CheckInService checkInService;
  private final RegistrationService registrationService;
  private final RegistrationMatchingService registrationMatchingService;
  private final ErrorLogService errorLogService;
  private final PersonService personService;
  private final UserService userService;
  @GetMapping(path = "/run-cancel")
  public void runCancel(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      eodCancelJob.runCancelJob();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-matching")
  @Transactional
  public void runMatching(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      registrationService.updateSoldOut(event);
      registrationMatchingService.doMatching(event);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {

      errorLogService.addLog("RUN-MATCHING", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-release")
  public void runRelease(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      eodReleaseJob.runRelease();
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/run-reminder")
  public void runReminder(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      eodReminderJob.runReminderJob();
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
