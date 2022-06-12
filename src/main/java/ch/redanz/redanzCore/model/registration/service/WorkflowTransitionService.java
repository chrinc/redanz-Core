package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.repository.WorkflowTransitionRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkflowTransitionService {
    private final WorkflowTransitionRepo workflowTransitionRepo;

    public void saveWorkflowTransition(WorkflowTransition workflowTransition) {
        workflowTransitionRepo.save(workflowTransition);
    }

    public WorkflowTransition findFirstByRegistrationOrderByTransitionTimestampDesc(Registration registration) {
        return workflowTransitionRepo.findFirstByRegistrationOrderByTransitionTimestampDesc(registration);
    }
}
