package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkflowTransitionRepo extends JpaRepository<WorkflowTransition, Long> {
  WorkflowTransition findFirstByRegistrationOrderByTransitionTimestampDescWorkflowStatusDesc(Registration registration);
}
