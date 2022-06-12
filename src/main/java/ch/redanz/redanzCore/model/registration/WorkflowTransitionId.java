package ch.redanz.redanzCore.model.registration;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WorkflowTransitionId implements Serializable {
    private Long workflowStatusId;
    private Long registrationId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkflowTransitionId)) return false;
        WorkflowTransitionId that = (WorkflowTransitionId) o;
        return getWorkflowStatusId().equals(that.getWorkflowStatusId()) && getRegistrationId().equals(that.getRegistrationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorkflowStatusId(), getRegistrationId());
    }

    public Long getWorkflowStatusId() {
        return workflowStatusId;
    }

    public void setWorkflowStatusId(Long workflowStatusId) {
        this.workflowStatusId = workflowStatusId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }
}
