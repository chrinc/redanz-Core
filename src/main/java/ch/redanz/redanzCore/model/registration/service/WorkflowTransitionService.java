package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.WorkflowTransitionRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class WorkflowTransitionService {
  private final WorkflowTransitionRepo workflowTransitionRepo;
  private final RegistrationRepo registrationRepo;

  public void save(WorkflowTransition workflowTransition) {
    workflowTransitionRepo.save(workflowTransition);
  }

  public WorkflowTransition findFirstByRegistrationOrderByTransitionTimestampDesc(Registration registration) {
    return workflowTransitionRepo.findFirstByRegistrationOrderByTransitionTimestampDescWorkflowStatusDesc(registration);
  }

  public void setWorkflowStatus(Registration registration, WorkflowStatus workflowStatus) {
    ZonedDateTime currentTimestamp = ZonedDateTime.now();
    WorkflowTransition workflowTransition = new WorkflowTransition(
      workflowStatus,
      registration,
      currentTimestamp
    );
    workflowTransitionRepo.save(workflowTransition);
    registration.setWorkflowStatus(workflowStatus);
    registration.setWorkflowStatusDate(currentTimestamp);
    registrationRepo.save(registration);
  }
}
