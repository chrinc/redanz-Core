package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.workshop.Discount;
import ch.redanz.redanzCore.model.workshop.FoodRegistration;
import ch.redanz.redanzCore.model.workshop.Bundle;
import ch.redanz.redanzCore.model.workshop.DanceRole;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.Track;
import ch.redanz.redanzCore.model.profile.Person;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Component
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

//  @ManyToOne
//  @JoinColumn(name="partner_id")
//  private Person partner;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "registration")
  private List<WorkflowTransition> transitionList;

//  @OneToOne(cascade=CascadeType.ALL)
//  private RegistrationMatching registrationMatching;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "registration")
  private List<FoodRegistration> foodRegistrationList;

//  @OneToOne(cascade=CascadeType.ALL)
//  private RegistrationMatching registrationMatching;

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

  public DanceRole getDanceRole() {
    return danceRole;
  }

  public void setDanceRole(DanceRole danceRole) {
    this.danceRole = danceRole;
  }

  public Long getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(Long registrationId) {
    this.registrationId = registrationId;
  }

  public Person getParticipant() {
    return participant;
  }

  public void setParticipant(Person participant) {
    this.participant = participant;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

  public Bundle getBundle() {
    return bundle;
  }

  public void setBundle(Bundle bundle) {
    this.bundle = bundle;
  }

  public Track getTrack() {
    return track;
  }

  public void setTrack(Track track) {
    this.track = track;
  }

  public List<WorkflowTransition> getTransitionList() {
    return transitionList;
  }

  public void setTransitionList(List<WorkflowTransition> transitionList) {
    this.transitionList = transitionList;
  }

  public List<FoodRegistration> getFoodRegistrationList() {
    return foodRegistrationList;
  }

  public void setFoodRegistrationList(List<FoodRegistration> foodRegistrationList) {
    this.foodRegistrationList = foodRegistrationList;
  }

  public List<Discount> getDiscountList() {
    return discountList;
  }

  public void setDiscountList(List<Discount> discountList) {
    this.discountList = discountList;
  }

  //  @ManyToOne
//  @JoinColumn(name="dance_role_id")
//  private DanceRole danceRole;
//
//  @ManyToOne
//  @JoinColumn(name="dance_level_id")
//  private DanceLevel danceLevel;
//
//  @ManyToOne
//  @JoinColumn(name="bundle_id")
//  private Bundle bundle;
//
//  @ManyToOne
//  @JoinColumn(name="track_id")
//  private Track track;
//
//  @ManyToOne
//  @JoinColumn(name="event_id")
//  private Event event;
//


  public Registration () {}


//   getter
//   setter

}
