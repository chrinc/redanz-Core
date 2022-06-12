package ch.redanz.redanzCore.model.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@ToString
@Table(name="workflow_transition")
public class WorkflowTransition implements Serializable {

  @EmbeddedId
  private final WorkflowTransitionId workflowTransitionId = new WorkflowTransitionId();

  @ManyToOne
  @MapsId("workflowStatusId")
  @JoinColumn(name="workflow_status_id")
  private WorkflowStatus workflowStatus;

  @ManyToOne
  @MapsId("registrationId")
  @JsonIgnore
  @JoinColumn(name="registration_id")
  private Registration registration;

  @Column(name="transition_timestamp")
  private LocalDateTime transitionTimestamp;

  public WorkflowTransition () {}

  public WorkflowTransition(WorkflowStatus workflowStatus, Registration registration, LocalDateTime transitionTimestamp) {
    this.workflowStatus = workflowStatus;
    this.registration = registration;
    this.transitionTimestamp = transitionTimestamp;
  }

  public WorkflowStatus getWorkflowStatus() {
    return workflowStatus;
  }

  public void setWorkflowStatus(WorkflowStatus workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  public Registration getRegistration() {
    return registration;
  }

  public void setRegistration(Registration registration) {
    this.registration = registration;
  }

  public LocalDateTime getTransitionTimestamp() {
    return transitionTimestamp;
  }

  public void setTransitionTimestamp(LocalDateTime transitionTimestamp) {
    this.transitionTimestamp = transitionTimestamp;
  }
}
