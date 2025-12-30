package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZoneId;
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
@NoArgsConstructor
public class CheckIn implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "check_in_id", nullable = false, updatable = false)
  private Long checkInId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name="event_id")
  private Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name="registration_id")
  private Registration registration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name="guest_id")
  private Guest guest;

  @Column(name="check_in_name")
  private String checkinName;

  @Column(name="type")
  private String type;

  @Column(name="description")
  private String description;

  @Column(name="slots")
  private String slots;

  @Column(name="food")
  private String food;

  @Column(name="addons")
  private String addons;

  @Column(name="privates")
  private String privates;

  @Column(name="discounts")
  private String discounts;

  @Column(name="status")
  private String status;

  @Column(name="amount_due")
  private Long amountDue;

  @Column(name="total_amount")
  private Long totalAmount;

  @Column(name="color")
  private String color;

  @Column(name="dance_role")
  private String danceRole;

  @Column(name = "check_in_time")
  private ZonedDateTime checkInTime;

  public CheckIn(
    Event event,
    Guest guest,
    String checkInName,
    String type,
    String description,
    String slots,
    String color
  ) {
    this.event = event;
    this.guest = guest;
    this.checkinName = checkInName;
    this.type = type;
    this.description = description;
    this.slots = slots;
    this.color = color;
  }

  public CheckIn(
    Event event,
    Registration registration,
    String checkInName,
    String type,
    String description,
    String slots,
    String color
  ) {
    this.event = event;
    this.registration = registration;
    this.checkinName = checkInName;
    this.type = type;
    this.description = description;
    this.slots = slots;
    this.color = color;
  }

  public CheckIn(
    Event event,
    Registration registration,
    String checkInName,
    String type,
    String description,
    String slots,
    String food,
    String addons,
    String discounts,
    String privates,
    String status,
    Long amountDue,
    Long totalAmount,
    String color,
    String danceRole
  ) {
    this.event = event;
    this.registration = registration;
    this.checkinName = checkInName;
    this.type = type;
    this.description = description;
    this.slots = slots;
    this.food = food;
    this.addons = addons;
    this.discounts = discounts;
    this.privates = privates;
    this.status = status;
    this.color = color;
    this.amountDue = amountDue;
    this.totalAmount = totalAmount;
    this.danceRole = danceRole;
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
        add(new HashMap<>() {{
          put("key", "update");
          put("type", "update");
          put("label", "Check In");
        }});
      }
    };
  }
}
