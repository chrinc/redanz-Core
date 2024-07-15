package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@NoArgsConstructor
@Table(name = "event_bundle")
public class EventBundle implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_bundle_id")
  private Long eventBundleId;

  @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
  @JoinColumn(name = "bundle_id")
  private Bundle bundle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name = "event_id")
  private Event event;

  private Integer capacity;

  @Column(name = "sold_out")
  private boolean soldOut;

  public EventBundle(Bundle bundle, Event event, Integer capacity) {
    this.bundle = bundle;
    this.event = event;
    this.capacity = capacity;
  }
}

