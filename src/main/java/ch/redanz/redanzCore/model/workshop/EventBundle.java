package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="event_bundle")
@Slf4j
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

  public EventBundle() {}

  public EventBundle(Bundle bundle, Event event) {
    this.bundle = bundle;
    this.event = event;
  }

  public Bundle getBundle() {
    return bundle;
  }

  public void setBundle(Bundle bundle) {
    this.bundle = bundle;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }

}

