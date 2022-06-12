package ch.redanz.redanzCore.model.workshop;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Getter
@Setter
@Table(name="event")
@Slf4j
public class Event implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="event_id")
  private Long eventId;

  private String name;
  private Integer capacity;
  private String description;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "event")
  private Collection<EventBundle> eventBundles;

  public Event () {}

  public Event(String name, Integer capacity, String description) {
    this.name = name;
    this.capacity = capacity;
    this.description = description;
  }

//  public Long getEventId() {
//    return eventId;
//  }
//
//  public void setEventId(Long eventId) {
//    this.eventId = eventId;
//  }
//
//  public String getName() {
//    return name;
//  }
//
//  public void setName(String name) {
//    this.name = name;
//  }
//
//  public Integer getCapacity() {
//    return capacity;
//  }
//
//  public void setCapacity(Integer capacity) {
//    this.capacity = capacity;
//  }
//
//  public String getDescription() {
//    return description;
//  }
//
//  public void setDescription(String description) {
//    this.description = description;
//  }
//
//  public Collection<EventBundle> getEventBundles() {
//    return eventBundles;
//  }
//
//  public void setEventBundles(Collection<EventBundle> eventBundles) {
//    this.eventBundles = eventBundles;
//  }

}

