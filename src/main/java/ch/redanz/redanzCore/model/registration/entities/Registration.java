package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.profile.entities.Person;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Component
@Getter
@Setter

@Table(name="registration")
public class Registration implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "registration_id")
  private Long registrationId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="participant_id")
  private Person participant;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="bundle_id")
  private Bundle bundle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="track_id")
  private Track track;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="current_workflow_status_id")
  private WorkflowStatus workflowStatus;

  @Column(name = "current_transition_date")
  private Instant workflowStatusDate;

  @Column(name = "current_transition_date_tz")
  private String workflowStatusDateTz;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="dance_role_id")
  private DanceRole danceRole;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "registration")
  private List<WorkflowTransition> transitionList;

  @Column(name = "registration_type")
  @Enumerated(EnumType.STRING)
  private RegistrationType registrationType;

  private Boolean active;

  @ManyToMany(cascade=CascadeType.ALL)
  @JoinTable(
    name="discount_registration",
    joinColumns = @JoinColumn(name="registration_id"),
    inverseJoinColumns = @JoinColumn(name="discount_id")
  )
  private List<Discount> discountList;

  @Transient
  public ZonedDateTime getWorkflowStatusDate() {
    if (workflowStatusDate == null || workflowStatusDateTz == null) {
      return null;
    }
    return this.workflowStatusDate.atZone(ZoneId.of(workflowStatusDateTz));
  }

  @Transient
  public void setWorkflowStatusDate(ZonedDateTime zdt) {
    this.workflowStatusDateTz = zdt.getOffset().getId();
    this.workflowStatusDate = zdt.toInstant();
  }

  public Registration(Event event, Bundle bundle, Person participant, RegistrationType registrationType) {
    this.event = event;
    this.bundle = bundle;
    this.participant = participant;
    this.active = true;
    this.registrationType = registrationType;
  }

  public Registration(Person participant, Event event, Bundle bundle, Track track, DanceRole danceRole, RegistrationType registrationType) {
    this.participant = participant;
    this.event = event;
    this.bundle = bundle;
    this.track = track;
    this.danceRole = danceRole;
    this.active = true;
    this.registrationType = registrationType;

  }

  public Registration(Person participant, Event event, RegistrationType registrationType) {
    this.participant = participant;
    this.event = event;
    this.active = true;
    this.registrationType = registrationType;
  }

  public Registration () {}
}
