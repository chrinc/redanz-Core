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

  private boolean hosting;

  private boolean volunteering;
  private boolean scholarship;

  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.LAZY)
  private Collection<EventBundle> eventBundles;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.LAZY)
  @LazyCollection(LazyCollectionOption.FALSE)
  private Collection<EventDiscount> eventDiscounts;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  @LazyCollection(LazyCollectionOption.FALSE)
  private Collection<EventTypeSlot> eventTypeSlots;

  public Event() {
  }

  public Event(String name, Integer capacity, LocalDate eventFrom, LocalDate eventTo, ZonedDateTime registrationStart, boolean active, boolean archived, String description, boolean hosting, boolean volunteering, boolean scholarship) {
    this.name = name;
    this.capacity = capacity;
    this.eventFrom = eventFrom;
    this.eventTo = eventTo;
    this.registrationStart = registrationStart;
    this.active = active;
    this.archived = archived;
    this.description = description;
    this.hosting = hosting;
    this.volunteering = volunteering;
    this.scholarship = scholarship;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");        put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");      put("required", "true");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "eventFrom");            put("type", "date");        put("required", "true");   put("label", "From");}});
        add(new HashMap<>() {{put("key", "eventTo");              put("type", "date");        put("required", "true");   put("label", "To");}});
        add(new HashMap<>() {{put("key", "registrationStart");    put("type", "datetime");    put("required", "true");   put("label", "Registration Start");}});
        add(new HashMap<>() {{put("key", "active");               put("type", "bool");                                   put("labelTrue", "Active"); put("labelFalse", "Inactive"); }});
        add(new HashMap<>() {{put("key", "description");          put("type", "text");        put("required", "false");  put("label", "Description");}});
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                     put("label", "id");}});
        add(new HashMap<>() {{put("key", "isEdit");               put("type", "isEdit");                                 put("label", "");}});
        add(new HashMap<>() {{put("key", "bundle");               put("type", "attribute");                              put("label", "Bundles");}});
        add(new HashMap<>() {{put("key", "discount");             put("type", "attribute");                              put("label", "Discounts");}});
        add(new HashMap<>() {{put("key", "food");                 put("type", "attribute");                              put("label", "Food");}});
        add(new HashMap<>() {{put("key", "special");              put("type", "attribute");                              put("label", "Specials");}});
        add(new HashMap<>() {{put("key", "private");              put("type", "attribute");                              put("label", "Private Classes");}});
        add(new HashMap<>() {{put("key", "hosting");              put("type", "attribute");                              put("label", "Hosting");}});
        add(new HashMap<>() {{put("key", "volunteering");         put("type", "attribute");                              put("label", "Volunteering");}});
        add(new HashMap<>() {{put("key", "scholarship");          put("type", "attribute");                              put("label", "Scholarship");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("name", name);
        put("capacity", String.valueOf(capacity));
        put("eventFrom", eventFrom.toString() + "T00:00+0100");
        put("eventTo", eventTo.toString() + "T00:00+0100");
        put("registrationStart", registrationStart.toString());
        put("active", String.valueOf(active));
        put("description", description);
        put("id", String.valueOf(eventId));
      }
    };
  }
}

