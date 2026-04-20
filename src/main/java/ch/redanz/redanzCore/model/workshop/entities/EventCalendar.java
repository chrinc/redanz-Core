package ch.redanz.redanzCore.model.workshop.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_calendar")
public class EventCalendar implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @Column(name = "start", nullable = false)
  private ZonedDateTime startTime;

  @Column(name = "end", nullable = false)
  private ZonedDateTime endTime;

  private String title;
  private String description;
  private String venue;
  private Boolean active;


  @OneToMany(mappedBy = "eventCalendar", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<EventCalendarBookItem> bookItems = new ArrayList<>();

  public EventCalendar() {}
  public EventCalendar(Event event, ZonedDateTime startTime, ZonedDateTime endTime, String title, String description, String venue, Boolean active) {
    this.event = event;
    this.startTime = startTime;
    this.endTime = endTime;
    this.title = title;
    this.description = description;
    this.venue = venue;
    this.active = active;
  }
}

