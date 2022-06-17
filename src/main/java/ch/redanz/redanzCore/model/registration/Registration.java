package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.workshop.Discount;
import ch.redanz.redanzCore.model.workshop.Bundle;
import ch.redanz.redanzCore.model.workshop.DanceRole;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.Track;
import ch.redanz.redanzCore.model.profile.Person;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
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
