package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
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
    name = "event_volunteer_type",
    joinColumns = @JoinColumn(name = "event_id"),
    inverseJoinColumns = @JoinColumn(name = "volunteer_type_id"))
  private List<VolunteerType> volunteerTypes;

  private String name;

  @Column(name = "capacity")
  private Integer capacity;

  @Column(name = "sold_out")
  private boolean soldOut = false;

  @Column(name = "event_from")
  private LocalDate eventFrom;

  @Column(name = "event_to")
  private LocalDate eventTo;

  @Column(name = "registration_start", nullable = false)
  private ZonedDateTime registrationStart;

  private boolean active;

  private boolean archived;

  private boolean hosting;

  private boolean volunteering;
  private boolean scholarship;

  @Column(name = "require_terms")
  private boolean requireTerms;

  private String description;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventPrivateClass> eventPrivates;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventSpecial> eventSpecials;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventDiscount> eventDiscounts;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventBundle> eventBundles;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventTypeSlot> eventTypeSlots;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventFoodSlot> eventFoodSlots;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventTrack> eventTracks;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventPartInfo> eventPartInfoSet;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "event", fetch = FetchType.EAGER)
  private Set<EventDanceRole> eventDanceRoles;

  public Event() {
  }

  public Event(
    String name,
    Integer capacity,
    LocalDate eventFrom,
    LocalDate eventTo,
    ZonedDateTime registrationStart,
    boolean active,
    boolean archived,
    String description,
    boolean hosting,
    boolean volunteering,
    boolean scholarship,
    List<VolunteerType> volunteerTypes,
    boolean requireTerms
  ) {
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
    this.volunteerTypes = volunteerTypes;
    this.requireTerms = requireTerms;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");        put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");      put("required", "true");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "eventFrom");            put("type", "date");        put("required", "true");   put("label", "From");}});
        add(new HashMap<>() {{put("key", "eventTo");              put("type", "date");        put("required", "true");   put("label", "To");}});
        add(new HashMap<>() {{put("key", "registrationStart");    put("type", "datetime");    put("required", "true");   put("label", "Registration Start");}});
        add(new HashMap<>() {{put("key", "active");               put("type", "bool");                                   put("labelTrue", "Active");              put("labelFalse", "Inactive"); }});
        add(new HashMap<>() {{put("key", "archived");             put("type", "bool");                                   put("labelTrue", "Archived");            put("labelFalse", "Not Archived"); }});
        add(new HashMap<>() {{put("key", "description");          put("type", "text");        put("required", "false");  put("label", "Description");}});
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                     put("label", "id");}});
        add(new HashMap<>() {{put("key", "isEdit");               put("type", "isEdit");                                 put("label", "");}});
        add(new HashMap<>() {{put("key", "bundle");               put("type", "attribute");                              put("label", "Bundles");}});
        add(new HashMap<>() {{put("key", "track");                put("type", "attribute");                              put("label", "Tracks");}});
        add(new HashMap<>() {{put("key", "foodSlot");             put("type", "attribute");                              put("label", "Food");}});
        add(new HashMap<>() {{put("key", "eventDanceRole");         put("type", "attribute");                              put("label", "Dance Roles");}});
        add(new HashMap<>() {{put("key", "eventPrivate");         put("type", "attribute");                              put("label", "Private Classes");}});
        add(new HashMap<>() {{put("key", "eventSpecial");         put("type", "attribute");                              put("label", "Specials");}});
        add(new HashMap<>() {{put("key", "eventDiscount");        put("type", "attribute");                              put("label", "Discounts");}});
        add(new HashMap<>() {{put("key", "hosting");              put("type", "attribute");                              put("label", "Accommodation");}});
        add(new HashMap<>() {{put("key", "volunteering");         put("type", "attribute");                              put("label", "Volunteering");}});
        add(new HashMap<>() {{put("key", "scholarship");          put("type", "attribute");                              put("label", "Solidarity Fund");}});
        add(new HashMap<>() {{put("key", "requireTerms");         put("type", "attribute");                              put("label", "Terms");}});
        add(new HashMap<>() {{put("key", "clone");                put("type", "action");         put("show", "true");}});
        add(new HashMap<>() {{put("key", "plural");               put("type", "title");           put("label", OutTextConfig.LABEL_EVENTS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");             put("type", "title");           put("label", OutTextConfig.LABEL_EVENT_EN.getOutTextKey()); }});

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
        put("registrationStart", getRegistrationStart().toString());
        put("active", String.valueOf(active));
        put("archived", String.valueOf(archived));
        put("description", description);
        put("id", String.valueOf(eventId));
      }
    };
  }
}

