package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("core-api/app/register")
@AllArgsConstructor
public class RegistrationController {
  private final RegistrationService registrationService;
  private final WorkflowStatusService workflowStatusService;
  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationReleaseService registrationReleaseService;
  private final EventService eventService;
  private final BaseParService baseParService;
  private final WorkflowTransitionService workflowTransitionService;
  private final GuestService guestService;
  private final CheckInService checkInService;
  private final RegistrationEmailService registrationEmailService;
  private final ErrorLogService errorLogService;

  @Autowired
  Configuration mailConfig;

  @GetMapping(path = "/registration")
  @Transactional
  public RegistrationResponse getRegistration(
    @RequestParam("personId") Long personId,
    @RequestParam("eventId") Long eventId
  ) {
    try {
//      return null;
      RegistrationResponse response = registrationService.getRegistrationResponse(personId, eventId);
      Hibernate.initialize(response.getWorkflowStatus());
      return response;
    } catch (Exception exception) {
      errorLogService.addLog("/registration", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/registration/new")
  @Transactional
  public RegistrationResponse getNewRegistration(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      return registrationService.getNewRegistrationResponse(eventId);
    } catch (Exception exception) {
      errorLogService.addLog("/registration/new", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/AllUserRegistrations")
  @Transactional
  public List<RegistrationResponse> getAllUserRegistration(
    @RequestParam("personId") Long personId
  ) {
    try {
      return registrationService.getAllUserRegistrationResponses(personId);
    } catch (Exception exception) {
      errorLogService.addLog("/AllUserRegistrations", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/UserActiveRegistrations")
  @Transactional
  public List<RegistrationResponse> getUserActiveRegistration(
    @RequestParam("personId") Long personId
  ) {
    try {
      return registrationService.getUserActiveRegistrationResponses(personId);
    } catch (Exception exception) {
      errorLogService.addLog("/UserActiveRegistrations", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }


  @GetMapping(path = "/UserInactiveRegistrations")
  @Transactional
  public List<RegistrationResponse> getInactiveUserRegistration(
    @RequestParam("personId") Long personId
  ) {
    try {
      return registrationService.getUserInactiveRegistrationResponses(personId);
    } catch (Exception exception) {
      errorLogService.addLog("/UserInactiveRegistrations", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-match")
  @Transactional
  public void manualMatch(
    @RequestParam("registrationId1") Long registrationId1,
    @RequestParam("registrationId2") Long registrationId2
  ) {
    try {
      registrationMatchingService.doMatch(
        registrationService.findByRegistrationId(registrationId1),
        registrationService.findByRegistrationId(registrationId2)
      );
    } catch (Exception exception) {
      errorLogService.addLog("/manual-match", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-release")
  @Transactional
  public void manualRelease(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onManualRelease(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-release", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
  @GetMapping(path = "/manual-cancel")
  @Transactional
  public void manualCancel(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onCancel(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-cancel", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-delete")
  @Transactional
  public void manualDelete(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onDelete(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-delete", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-delete/host")
  @Transactional
  public void manualDeleteHost(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onDeleteHost(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-delete/host", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-delete/hostee")
  @Transactional
  public void manualDeleteHostee(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onDeleteHostee(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-delete/hostee", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
  @GetMapping(path = "/manual-delete/volunteer")
  @Transactional
  public void manualDeleteVolunteer(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      registrationService.onDeleteVolunteer(
        registrationService.findByRegistrationId(registrationId)
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-delete/volunteer", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-confirming")
  @Transactional
  public void manualConfirming(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      Registration registration = registrationService.findByRegistrationId(registrationId);
      registrationEmailService.postPoneLastReminderDate(registration);
      workflowTransitionService.setWorkflowStatus(
        registrationService.findByRegistrationId(registrationId),
        workflowStatusService.getConfirming()
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-confirming", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-submitted")
  @Transactional
  public void manualSubmitted(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      workflowTransitionService.setWorkflowStatus(
        registrationService.findByRegistrationId(registrationId),
        workflowStatusService.getSubmitted()
      );
    } catch (Exception exception) {
      errorLogService.addLog("manual-submitted", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/update")
  @Transactional
  public void update(
    @RequestParam("personId") Long personId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String jsonObject
  ) {
    try {
//      log.info("inc@start");
      Event event = eventService.findByEventId(eventId);
      registrationService.updateSoldOut(event);

      // update
      Registration registration = registrationService.updateRegistrationRequest(
        personId,
        event,
        JsonParser.parseString(jsonObject).getAsJsonObject()

      );

//      log.info("bfr automatch");

      // match
      if (baseParService.doAutoMatch()) {
//        log.info("bfr updateSoldOut");
        registrationService.updateSoldOut(event);
//        log.info("bfr doMatching");
        registrationMatchingService.doMatching(registration);
      }

//      log.info("bfr release");
      // release
      if (baseParService.doAutoRelease()) {
//      log.info("bfr updateSoldOut");
        registrationService.updateSoldOut(event);
//      log.info("bfr doRelease");
        registrationReleaseService.doRelease(registration);
//      log.info("bfr updateSoldOut");
        registrationService.updateSoldOut(event);
      }

    } catch (ApiRequestException apiRequestException) {
      errorLogService.addLog("registrationUpdate", apiRequestException.getMessage());
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      errorLogService.addLog("registrationUpdate", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }

  @PostMapping(path = "/staff/update")
  @Transactional
  public void updateStaffRequest(
    @RequestParam("userId") Long userId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String jsonObject
  ) {
    try {
      Event event = eventService.findByEventId(eventId);

      // update
      registrationService.updateStaffRegistrationRequest(
        event,
        JsonParser.parseString(jsonObject).getAsJsonObject()

      );

    } catch (ApiRequestException apiRequestException) {
      errorLogService.addLog("staffUpdateApi", apiRequestException.toString());
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      errorLogService.addLog("StaffUpdate", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/workflow/status/all")
  public List<WorkflowStatus> getWorkflowStatusList() {
    return workflowStatusService.findAllPublic();
  }

  @GetMapping(path = "/confirming/reminder")
  public void sendConfirmingReminder() {
  }

  @PostMapping(path = "/guests/update")
  @Transactional
  public void guestsUpdate(
    @RequestParam("personId") Long personId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String guestsJsonObject
  ) {
    try {
      // log.info("update guest List");
      Event event = eventService.findByEventId(eventId);
      guestService.updateGuestListRequest(JsonParser.parseString(guestsJsonObject).getAsJsonArray(), event);
    } catch (ApiRequestException apiRequestException) {
      errorLogService.addLog("GuestsUpdateApi", apiRequestException.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    } catch (Exception exception) {
      errorLogService.addLog("GuestsUpdate", exception.toString());
      throw new ApiRequestException(exception.getMessage());
    }
  }

  @PostMapping(path = "/guest/remv")
  @Transactional
  public void remvGuest(
    @RequestParam("personId") Long personId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String guestJsonObject
  ) {
    try {
      // log.info("remove guest");
      Event event = eventService.findByEventId(eventId);
      guestService.removeGuest(JsonParser.parseString(guestJsonObject).getAsJsonObject(), event);
    } catch (ApiRequestException apiRequestException) {
      errorLogService.addLog("GuestsRemoveApi", apiRequestException.toString());
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      errorLogService.addLog("GuestsRemove", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }
  @PostMapping(path = "/checkIn")
  @Transactional
  public void checkIn(
    @RequestParam("personId") Long personId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String guestJsonObject
  ) {
    try {
      // log.info("remove guest");
      Event event = eventService.findByEventId(eventId);
      checkInService.checkInRequest(JsonParser.parseString(guestJsonObject).getAsJsonObject());
    } catch (ApiRequestException apiRequestException) {
      errorLogService.addLog("CheckInApi", apiRequestException.toString());
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      errorLogService.addLog("CheckIn", exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }
}
