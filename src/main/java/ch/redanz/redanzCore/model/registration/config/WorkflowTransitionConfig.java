package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.WorkflowStatusRepo;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Slf4j
public enum WorkflowTransitionConfig {

  // Submit
  SUBMIT1(WorkflowStatusConfig.SUBMITTED,  UserConfig.FRANKY_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT2(WorkflowStatusConfig.SUBMITTED,  UserConfig.NORMA_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT3(WorkflowStatusConfig.SUBMITTED,  UserConfig.EDDIE_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT4(WorkflowStatusConfig.SUBMITTED,  UserConfig.WILLIAM_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT5(WorkflowStatusConfig.SUBMITTED,  UserConfig.ELNORA_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT6(WorkflowStatusConfig.SUBMITTED,  UserConfig.ARLYNE_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT7(WorkflowStatusConfig.SUBMITTED,  UserConfig.BILLY_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT8(WorkflowStatusConfig.SUBMITTED,  UserConfig.NAOMI_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT9(WorkflowStatusConfig.SUBMITTED,  UserConfig.ESTHER_USER.getEmail(), EventConfig.EVENT2022),
  SUBMIT10(WorkflowStatusConfig.SUBMITTED, UserConfig.CLAUDIA_USER.getEmail(), EventConfig.EVENT2022),


  DONE1(WorkflowStatusConfig.DONE,  UserConfig.FRANKY_USER.getEmail(), EventConfig.EVENT2022),
  DONE2(WorkflowStatusConfig.DONE,  UserConfig.NORMA_USER.getEmail(), EventConfig.EVENT2022),
  DONE3(WorkflowStatusConfig.DONE,  UserConfig.BILLY_USER.getEmail(), EventConfig.EVENT2022),
  DONE4(WorkflowStatusConfig.DONE,  UserConfig.NAOMI_USER.getEmail(), EventConfig.EVENT2022);

  private final WorkflowStatusConfig workflowStatusConfig;
  private final String userEmail;
  private final EventConfig eventConfig;

  public static List<WorkflowTransition> setup(
    EventRepo eventRepo,
    RegistrationRepo registrationRepo,
    PersonRepo personRepo,
    UserRepo userRepo,
    WorkflowStatusRepo workflowStatusRepo,
    WorkflowStatusConfig appliedWorkflowStatusConfig
  ) {
    List<WorkflowTransition> transitions = new ArrayList<>();

    for (WorkflowTransitionConfig workflowTransitionConfig : WorkflowTransitionConfig.values()) {
      if (appliedWorkflowStatusConfig.equals(workflowTransitionConfig.workflowStatusConfig)) {
        transitions.add(
          new WorkflowTransition(
            workflowStatusRepo.findByName(workflowTransitionConfig.workflowStatusConfig.getName()),
            registrationRepo.findByParticipantAndEvent(
              personRepo.findByUser(userRepo.findByEmail(workflowTransitionConfig.userEmail)),
              eventRepo.findByName(workflowTransitionConfig.eventConfig.getName())
            ).get()
            , LocalDateTime.now()
          )
        );
      }
    }
    return transitions;
  }
}