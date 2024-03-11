package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface WorkflowStatusRepo extends JpaRepository<WorkflowStatus, Long> {
    WorkflowStatus findByWorkflowStatusId(Long workflowId);
    WorkflowStatus findByName(String name);
    boolean existsByName(String name);
}
