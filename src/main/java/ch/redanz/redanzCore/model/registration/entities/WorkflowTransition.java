package ch.redanz.redanzCore.model.registration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
//  @JsonIgnore
//  @Transient
//  private RegistrationRepo registrationRepo;

//  @EmbeddedId
//  private final WorkflowTransitionId workflowTransitionId = new WorkflowTransitionId();

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "workflow_transition_id")
  private Long workflowTransitionId;

  @ManyToOne
//  @MapsId("workflowStatusId")
  @JoinColumn(name="workflow_status_id")
  private WorkflowStatus workflowStatus;

  @ManyToOne
//  @MapsId("registrationId")
  @JsonIgnore
  @JoinColumn(name="registration_id")
  private Registration registration;

  @Column(name="transition_timestamp")
  private Instant transitionTimestamp;

  @Column(name="transition_timestamp_tz")
  private String transitionTimestampTz;
  @Transient
  public ZonedDateTime getTransitionTimestamp() {
    if (transitionTimestamp == null || transitionTimestampTz == null) {
      return null;
    }
    return transitionTimestamp.atZone(ZoneId.of(transitionTimestampTz));
  }

  @Transient
  public void setTransitionTimestamp(ZonedDateTime zdt) {
    this.transitionTimestamp = zdt.toInstant();
    this.transitionTimestampTz = zdt.getOffset().getId();
  }

  public WorkflowTransition(
    WorkflowStatus workflowStatus
    , Registration registration
    , ZonedDateTime transitionTimestamp
  ) {
    this.workflowStatus = workflowStatus;
    this.registration = registration;
    setTransitionTimestamp(transitionTimestamp);
  }
}
