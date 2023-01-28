package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
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
  private final EventService eventService;

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
    @RequestParam("userId") Long userId,
    @RequestParam("user2Id") Long user2Id
  ) {
    try {
      registrationMatchingService.doMatch(
        registrationService.getRegistration(
          userId,
          eventService.getCurrentEvent()
        ),
        registrationService.getRegistration(
          user2Id,
          eventService.getCurrentEvent()
        )
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/manual-release")
  @Transactional
  public void manualRelease(
    @RequestParam("userId") Long userId
  ) {
    try {
      registrationService.onManualRelease(
        registrationService.getRegistration(
          userId,
          eventService.getCurrentEvent()
        )
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
  @GetMapping(path = "/manual-cancel")
  @Transactional
  public void manualCancel(
    @RequestParam("userId") Long userId
  ) {
    try {
      registrationService.onCancel(
        registrationService.getRegistration(
          userId,
          eventService.getCurrentEvent()
        )
      );
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/update")
  @Transactional
  public void update(
    @RequestParam("userId") Long userId,
    @RequestBody String jsonObject
  ) {
    try {
      registrationService.updateRegistrationRequest(
        userId,
        JsonParser.parseString(jsonObject).getAsJsonObject()
      );
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }

  @GetMapping(path = "/workflow/status/all")
  public List<WorkflowStatus> getWorkflowStatusList() {
    List<WorkflowStatus> workflowStatusList = registrationService.getWorkflowStatusList();
    workflowStatusList.remove(
      workflowStatusService.getCancelled()
    );
    return workflowStatusList;
  }

  @GetMapping(path = "/confirming/reminder")
  public void sendConfirmingReminder() {
  }
}
