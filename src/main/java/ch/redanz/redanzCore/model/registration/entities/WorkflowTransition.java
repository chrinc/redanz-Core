package ch.redanz.redanzCore.model.registration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

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
  private LocalDateTime transitionTimestamp;



  public WorkflowTransition(
    WorkflowStatus workflowStatus
    , Registration registration
    , LocalDateTime transitionTimestamp
  ) {
    this.workflowStatus = workflowStatus;
    this.registration = registration;
    this.transitionTimestamp = transitionTimestamp;
//    updateRegistration();
  }

//  private void updateRegistration() {
//    Registration updateRegistration = this.registration;
//    log.info("this: {}",   this);
//    updateRegistration.setWorkflowTransition(this);
//    log.info("avter save inc001");
//    log.info("avter save inc001 firstName {}", updateRegistration.getParticipant().getFirstName());
//    log.info("avter save inc001 firstName 2? {}", updateRegistration.getWorkflowTransition().getRegistration().getParticipant().getFirstName());
////    registrationRepo.save(updateRegistration);
//
//    log.info("avter save registration");
//  }
}
