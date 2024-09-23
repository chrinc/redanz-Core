package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "check_in")
@Getter
@Setter
@AllArgsConstructor
public class CheckIn implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "check_in_id", nullable = false, updatable = false)
  private Long checkInId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="registration_id")
  private Registration registration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="guest_id")
  private Guest guest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name="event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="slot_id")
  private Slot slot;

  @Column(name = "check_in_time")
  private ZonedDateTime checkInTime;

  public CheckIn() {
  }
  public CheckIn(
    Registration registration,
    Event event,
    Slot slot
  ) {
    this.registration = registration;
    this.event = event;
    this.slot = slot;
  }
  public CheckIn(
    Guest guest,
    Event event,
    Slot slot
  ) {
    this.guest = guest;
    this.event = event;
    this.slot = slot;
  }
  public CheckIn(
    Registration registration,
    Event event
  ) {
    this.registration = registration;
    this.event = event;
  }
  public CheckIn(
    Guest guest,
    Event event
  ) {
    this.guest = guest;
    this.event = event;
  }

  public static List<Map<String, String>> schema() {

    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "checkInId");
          put("type", "id");
          put("label", "Check In");
        }});
        add(new HashMap<>() {{
          put("key", "registration");
          put("type", "id");
          put("label", "Registration");
        }});
//        add(new HashMap<>() {{
//          put("eventPartKey", "slot");
//          put("type", "id");
//          put("label", "Slot");
//        }});
        add(new HashMap<>() {{
          put("key", "update");
          put("type", "update");
          put("label", "Check In");
        }});
      }
    };
  }
}
