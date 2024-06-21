package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.configTest.EventConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum WorkflowTransitionConfig {

  // Submit
  SUBMIT1(WorkflowStatusConfig.SUBMITTED,  UserConfig.FRANKY_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT2(WorkflowStatusConfig.SUBMITTED,  UserConfig.NORMA_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT4(WorkflowStatusConfig.SUBMITTED,  UserConfig.WILLIAM_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT9(WorkflowStatusConfig.SUBMITTED,  UserConfig.ESTHER_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT10(WorkflowStatusConfig.SUBMITTED, UserConfig.CLAUDIA_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT11(WorkflowStatusConfig.SUBMITTED, UserConfig.OLIVER_USER.getUsername(), EventConfig.REDANZ_EVENT),
  SUBMIT12(WorkflowStatusConfig.SUBMITTED, UserConfig.HARRY_USER.getUsername(), EventConfig.REDANZ_EVENT),

//  SUBMIT3(WorkflowStatusConfig.SUBMITTED,  UserConfig.EDDIE_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  SUBMIT5(WorkflowStatusConfig.SUBMITTED,  UserConfig.ELNORA_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  SUBMIT6(WorkflowStatusConfig.SUBMITTED,  UserConfig.ARLYNE_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  SUBMIT7(WorkflowStatusConfig.SUBMITTED,  UserConfig.BILLY_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  SUBMIT8(WorkflowStatusConfig.SUBMITTED,  UserConfig.NAOMI_USER.getUsername(), EventConfig.REDANZ_EVENT),


//  DONE1(WorkflowStatusConfig.DONE,  UserConfig.FRANKY_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  DONE2(WorkflowStatusConfig.DONE,  UserConfig.NORMA_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  DONE3(WorkflowStatusConfig.DONE,  UserConfig.BILLY_USER.getUsername(), EventConfig.REDANZ_EVENT),
//  DONE4(WorkflowStatusConfig.DONE,  UserConfig.NAOMI_USER.getUsername(), EventConfig.REDANZ_EVENT);
;
  private final WorkflowStatusConfig workflowStatusConfig;
  private final String userEmail;
  private final EventConfig eventConfig;

  public static void setup(
    EventService eventService,
    RegistrationService registrationService,
    PersonService personService,
    UserService userService,
    WorkflowStatusService workflowStatusService,
    WorkflowStatusConfig appliedWorkflowStatusConfig,
    WorkflowTransitionService workflowTransitionService
  ) {
    for (WorkflowTransitionConfig workflowTransitionConfig : WorkflowTransitionConfig.values()) {
      if (appliedWorkflowStatusConfig.equals(workflowTransitionConfig.workflowStatusConfig)) {
          workflowTransitionService.setWorkflowStatus(
            registrationService.findByParticipantAndEvent(
              personService.findByUser(userService.getUser(workflowTransitionConfig.userEmail)),
              eventService.findByName(workflowTransitionConfig.eventConfig.getName()),
              RegistrationType.PARTICIPANT
            ).get(),
            workflowStatusService.findByWorkflowStatusName(workflowTransitionConfig.workflowStatusConfig.getName())
          );
      }
    }
  }
}
