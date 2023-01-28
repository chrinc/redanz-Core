package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;

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

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  private Collection<EventBundle> eventBundles;

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

}

