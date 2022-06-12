package ch.redanz.redanzCore.model.workshop;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventBundleId implements Serializable {

  private Long bundleId;
  private Long eventId;

  public Long getBundleId() {
    return bundleId;
  }

  public void setBundleId(Long bundleId) {
    this.bundleId = bundleId;
  }

  public Long getEventId() {
    return eventId;
  }

  public void setEventId(Long eventId) {
    this.eventId = eventId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventBundleId)) return false;
    EventBundleId that = (EventBundleId) o;
    return eventId.equals(that.eventId) &&
      bundleId.equals(that.bundleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId, bundleId);
  }
}