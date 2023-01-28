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
import java.time.LocalDateTime;
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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="participant_id")
  private Person participant;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="bundle_id")
  private Bundle bundle;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="track_id")
  private Track track;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="current_workflow_status_id")
  private WorkflowStatus workflowStatus;

  @Column(name = "current_transition_date")
  private LocalDateTime workflowStatusDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="dance_role_id")
  private DanceRole danceRole;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "registration")
  private List<WorkflowTransition> transitionList;

  @ManyToMany(cascade=CascadeType.ALL)
  @JoinTable(
    name="discount_registration",
    joinColumns = @JoinColumn(name="registration_id"),
    inverseJoinColumns = @JoinColumn(name="discount_id")
  )
  private List<Discount> discountList;

  public Registration(Event event, Bundle bundle, Person participant) {
    this.event = event;
    this.bundle = bundle;
    this.participant = participant;
  }

  public Registration(Person participant, Event event, Bundle bundle, Track track, DanceRole danceRole) {
    this.participant = participant;
    this.event = event;
    this.bundle = bundle;
    this.track = track;
    this.danceRole = danceRole;
  }

  public Registration () {}
}
