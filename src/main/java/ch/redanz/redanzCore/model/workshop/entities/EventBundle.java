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

  @EmbeddedId
  private final EventBundleId eventBundleId = new EventBundleId();

  @ManyToOne
  @MapsId("bundleId")
  @JoinColumn(name = "bundle_id")
  private Bundle bundle;

  @ManyToOne()
  @JsonIgnore
  @MapsId("eventId")
  @JoinColumn(name = "event_id")
  private Event event;

  public EventBundle() {
  }

  public EventBundle(Bundle bundle, Event event) {
    this.bundle = bundle;
    this.event = event;
  }
}

