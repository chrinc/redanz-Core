package ch.redanz.redanzCore.model.registration.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

}
