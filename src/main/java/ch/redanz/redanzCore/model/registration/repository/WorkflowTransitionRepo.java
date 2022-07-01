package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransitionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowTransitionRepo extends JpaRepository<WorkflowTransition, WorkflowTransitionId> {
  WorkflowTransition findFirstByRegistrationOrderByTransitionTimestampDesc(Registration registration);
  List<WorkflowTransition> findAllByWorkflowStatus(WorkflowStatus workflowStatus);

}
