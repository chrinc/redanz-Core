package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum WorkflowTransitionConfig {

  // Submit
  SUBMIT1(WorkflowStatusConfig.SUBMITTED,  UserConfig.FRANKY_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT2(WorkflowStatusConfig.SUBMITTED,  UserConfig.NORMA_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT3(WorkflowStatusConfig.SUBMITTED,  UserConfig.EDDIE_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT4(WorkflowStatusConfig.SUBMITTED,  UserConfig.WILLIAM_USER.getEmail(), EventConfig.EVENT2023),
//  SUBMIT5(WorkflowStatusConfig.SUBMITTED,  UserConfig.ELNORA_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT6(WorkflowStatusConfig.SUBMITTED,  UserConfig.ARLYNE_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT7(WorkflowStatusConfig.SUBMITTED,  UserConfig.BILLY_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT8(WorkflowStatusConfig.SUBMITTED,  UserConfig.NAOMI_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT9(WorkflowStatusConfig.SUBMITTED,  UserConfig.ESTHER_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT10(WorkflowStatusConfig.SUBMITTED, UserConfig.CLAUDIA_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT11(WorkflowStatusConfig.SUBMITTED,  UserConfig.OLIVER_USER.getEmail(), EventConfig.EVENT2023),
  SUBMIT12(WorkflowStatusConfig.SUBMITTED, UserConfig.HARRY_USER.getEmail(), EventConfig.EVENT2023),


  DONE1(WorkflowStatusConfig.DONE,  UserConfig.FRANKY_USER.getEmail(), EventConfig.EVENT2023),
  DONE2(WorkflowStatusConfig.DONE,  UserConfig.NORMA_USER.getEmail(), EventConfig.EVENT2023),
  DONE3(WorkflowStatusConfig.DONE,  UserConfig.BILLY_USER.getEmail(), EventConfig.EVENT2023),
  DONE4(WorkflowStatusConfig.DONE,  UserConfig.NAOMI_USER.getEmail(), EventConfig.EVENT2023);

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
              eventService.findByName(workflowTransitionConfig.eventConfig.getName())
            ).get(),
            workflowStatusService.findByWorkflowStatusName(workflowTransitionConfig.workflowStatusConfig.getName())
          );
      }
    }
  }
}
