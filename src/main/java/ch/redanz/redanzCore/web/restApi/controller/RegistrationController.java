package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonParser;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("core-api/app/register")
@AllArgsConstructor
public class RegistrationController {
  private final RegistrationService registrationService;
  private final WorkflowStatusService workflowStatusService;
  private final EventService eventService;


  @Autowired
  private Environment environment;

  @Autowired
  Configuration mailConfig;

  @GetMapping(path="/registration")
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
  @GetMapping(path="/manual-match")
  @Transactional
  public void manualMatch(
          @RequestParam("userId") Long userId,
          @RequestParam("user2Id") Long user2Id // @todo: add event Id
  ) {
    try {
      registrationService.doMatch(
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

  @PostMapping(path="/submit")
  @Transactional
  public void register(
          @RequestParam("userId") Long userId,
          @RequestBody String jsonObject
  ) throws IOException, TemplateException {
    log.info("inc, userId: {}", userId);
    try {
      registrationService.submitRegistration(
        userId,
        JsonParser.parseString(jsonObject).getAsJsonObject(),
        environment.getProperty("link.login")
      );
    } catch(Exception exception) {
        log.info("inc, throw api Request Exception?");
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
    }
  }

  @GetMapping(path="/workflow/status/all")
  public List<WorkflowStatus> getWorkflowStatusList() {
    List<WorkflowStatus> workflowStatusList = registrationService.getWorkflowStatusList();
    workflowStatusList.remove(
            workflowStatusList.indexOf(
                    workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.CANCELLED.getName())
            )
    );
    return workflowStatusList;
  }
  @GetMapping(path="/confirming/reminder")
  public void sendConfirmingReminder() {
//    registrationService.sendConfirmingReminder();
  }
}
