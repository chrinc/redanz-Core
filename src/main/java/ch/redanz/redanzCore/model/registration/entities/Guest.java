package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "guest")
@Getter
@Setter
@AllArgsConstructor
public class Guest implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "guest_id", nullable = false, updatable = false)
  private Long guestId;

  @Column(name = "name")
  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "person_id")
  private Person person;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @JsonIgnore
  private boolean active;

  @ManyToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
    name="guest_slots",
    joinColumns = @JoinColumn(name="guest_id"),
    inverseJoinColumns = @JoinColumn(name="event_slot_id")
  )
  private List<EventSlot> slots;

  private String description;

  public Guest() {
  }

  public Guest(
    String name,
    String description,
    Event event,
    List<EventSlot> slots,
    boolean active,
    Person person

  ) {
    this.name = name;
    this.description = description;
    this.event = event;
    this.slots = slots;
    this.active = active;
    this.person = person;
  }

  public Guest(
    Event event,
    Person person,
    List<EventSlot> slots,
    boolean active
  ) {
    this.person = person;
    this.event = event;
    this.slots = slots;
    this.active = active;
  }

  public static List<Map<String, String>> schema() {

    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "name");
          put("type", "text");
          put("label", "Name");
        }});
        add(new HashMap<>() {{
          put("key", "description");
          put("type", "text");
          put("label", "Description");
        }});
        add(new HashMap<>() {{
          put("key", "slots");
          put("type", "multiple");
          put("restriction", "party");
          put("label", "Slots");
          put("elemKey", "slotId");
          put("elemLabel", "name");
        }});
        add(new HashMap<>() {{
          put("key", "person");
          put("type", "list");
          put("restriction", "organizer");
          put("label", "Added By");
          put("elemKey", "personId");
          put("elemLabel", "firstName");
        }});
//        add(new HashMap<>() {{
//          put("eventPartKey", "person");
//          put("type", "id");
//          put("label", "Person");
//        }});
        add(new HashMap<>() {{
          put("key", "isEdit");
          put("type", "isEdit");
          put("label", "Edit");
        }});

        add(new HashMap<>() {{
          put("key", "isRemove");
          put("type", "isRemove");
          put("label", "LABEL-ACTION-DELETE");
        }});
      }
    };
  }
}
