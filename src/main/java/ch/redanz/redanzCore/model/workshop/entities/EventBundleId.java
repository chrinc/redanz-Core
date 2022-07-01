package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class EventBundleId implements Serializable {

  private Long bundleId;
  private Long eventId;

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
