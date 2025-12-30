package ch.redanz.redanzCore.model.registration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@ToString
@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Table(name="workflow_transition")
@AllArgsConstructor
public class WorkflowTransition implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "workflow_transition_id")
  private Long workflowTransitionId;

  @ManyToOne
  @JoinColumn(name="workflow_status_id")
  private WorkflowStatus workflowStatus;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name="registration_id")
  private Registration registration;

  @Column(name="transition_timestamp")
  private ZonedDateTime transitionTimestamp;

  public WorkflowTransition(
    WorkflowStatus workflowStatus
    , Registration registration
    , ZonedDateTime transitionTimestamp
  ) {
    this.workflowStatus = workflowStatus;
    this.registration = registration;
    this.transitionTimestamp = transitionTimestamp;
  }
}
