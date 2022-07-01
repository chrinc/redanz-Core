package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

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

  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "event", fetch = FetchType.EAGER)
  private Collection<EventBundle> eventBundles;

  public Event() {
  }

  public Event(String name, Integer capacity, String description) {
    this.name = name;
    this.capacity = capacity;
    this.description = description;
  }

}

