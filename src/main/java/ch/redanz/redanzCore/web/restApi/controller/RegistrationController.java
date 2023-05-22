package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  @Autowired
  Configuration mailConfig;

  @GetMapping(path = "/registration")
  @Transactional
  public RegistrationResponse getRegistration(
    @RequestParam("userId") Long userId,
    @RequestParam("eventId") Long eventId
  ) {
    try {
      return registrationService.getRegistrationResponse(userId, eventId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/AllUserRegistrations")
  @Transactional
  public List<RegistrationResponse> getAllUserRegistration(
    @RequestParam("userId") Long userId
  ) {
    try {
      return registrationService.getAllUserRegistrationResponses(userId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/UserActiveRegistrations")
  @Transactional
  public List<RegistrationResponse> getUserActiveRegistration(
    @RequestParam("userId") Long userId
  ) {
    try {
      return registrationService.getUserActiveRegistrationResponses(userId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }


  @GetMapping(path = "/UserInactiveRegistrations")
  @Transactional
  public List<RegistrationResponse> getInactiveUserRegistration(
    @RequestParam("userId") Long userId
  ) {
    try {
      return registrationService.getUserInactiveRegistrationResponses(userId);
    } catch (Exception exception) {
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
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-confirming")
  @Transactional
  public void manualConfirming(
    @RequestParam("registrationId") Long registrationId
  ) {
    try {
      workflowTransitionService.setWorkflowStatus(
        registrationService.findByRegistrationId(registrationId),
        workflowStatusService.getConfirming()
      );
    } catch (Exception exception) {
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
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/update")
  @Transactional
  public void update(
    @RequestParam("userId") Long userId,
    @RequestParam("eventId") Long eventId,
    @RequestBody String jsonObject
  ) {
    try {
      Event event = eventService.findByEventId(eventId);
      registrationService.updateSoldOut(event);

      // update
      Registration registration = registrationService.updateRegistrationRequest(
        userId,
        event,
        JsonParser.parseString(jsonObject).getAsJsonObject()

      );

      // match
      if (baseParService.doAutoMatch()) {
        registrationService.updateSoldOut(event);
        registrationMatchingService.doMatching(registration);
      }

      // release
      if (baseParService.doAutoRelease()) {
        registrationService.updateSoldOut(event);
        registrationReleaseService.doRelease(registration);
        registrationService.updateSoldOut(event);
      }

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
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
}
