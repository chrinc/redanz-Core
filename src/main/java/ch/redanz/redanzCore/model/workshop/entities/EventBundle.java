package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_bundle")
public class EventBundle implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_bundle_id")
  private Long eventBundleId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "bundle_id")
  private Bundle bundle;

  @ManyToOne(fetch = FetchType.LAZY)
  @JsonIgnore
  @JoinColumn(name = "event_id")
  private Event event;

  public EventBundle() {
  }

  public EventBundle(Bundle bundle, Event event) {
    this.bundle = bundle;
    this.event = event;
  }
}

