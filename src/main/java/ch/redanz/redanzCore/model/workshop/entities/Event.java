package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "event")
@Slf4j
public class Event implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_id")
  private Long eventId;

  @ManyToMany
  @JoinTable(
    name = "event_special",
    joinColumns = @JoinColumn(name = "event_id"),
    inverseJoinColumns = @JoinColumn(name = "special_id"))
  private List<Special> specials;

  @ManyToMany
  @JoinTable(
    name = "event_private_class",
    joinColumns = @JoinColumn(name = "event_id"),
    inverseJoinColumns = @JoinColumn(name = "private_class_id"))
  private List<PrivateClass> privateClasses;

  @ManyToMany
  @JoinTable(
    name = "event_volunteer_type",
    joinColumns = @JoinColumn(name = "event_id"),
    inverseJoinColumns = @JoinColumn(name = "volunteer_type_id"))
  private List<VolunteerType> volunteerTypes;

  private String name;
  private Integer capacity;

  @Column(name = "sold_out")
  private boolean soldOut = false;

  @Column(name = "event_from")
  private LocalDate eventFrom;

  @Column(name = "event_to")
  private LocalDate eventTo;

  @Column(name = "registration_start")
  private ZonedDateTime registrationStart;

  private boolean active;

  private boolean archived;

  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.LAZY)
  private Collection<EventBundle> eventBundles;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  @LazyCollection(LazyCollectionOption.FALSE)
  private Collection<EventTypeSlot> eventTypeSlots;

  public Event() {
  }

  public Event(String name, Integer capacity, LocalDate eventFrom, LocalDate eventTo, ZonedDateTime registrationStart, boolean active, boolean archived, String description) {
    this.name = name;
    this.capacity = capacity;
    this.eventFrom = eventFrom;
    this.eventTo = eventTo;
    this.registrationStart = registrationStart;
    this.active = active;
    this.archived = archived;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");     put("label", "Name");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "eventFrom");            put("type", "date");     put("label", "From");}});
        add(new HashMap<>() {{put("key", "eventTo");              put("type", "date");     put("label", "To");}});
//        add(new HashMap<>() {{put("key", "registrationStart");    put("type", "time");     put("label", "Registratin Start");}});
        add(new HashMap<>() {{put("key", "active");               put("type", "bool");     put("label", "Active");}});
//        add(new HashMap<>() {{put("key", "archived");             put("type", "bool");     put("label", "Archived");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "text");     put("label", "Description");}});
        add(new HashMap<>() {{put("key", "isEdit");               put("type", "isEdit");   put("label", "");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("name", name);
        put("capacity", String.valueOf(capacity));
        put("eventFrom", eventFrom.toString());
        put("eventTo", eventTo.toString());
//        put("registrationStart", registrationStart.toString());
        put("active", String.valueOf(active));
//        put("archived", String.valueOf(archived));
        put("description", description);
//        put("isEdit", true)
      }
    };
  }
}

