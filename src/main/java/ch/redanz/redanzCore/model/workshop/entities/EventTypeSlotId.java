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
public class EventTypeSlotId implements Serializable {
  private Long typeSlotId;
  private Long eventId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventTypeSlotId)) return false;
    EventTypeSlotId that = (EventTypeSlotId) o;
    return Objects.equals(getTypeSlotId(), that.getTypeSlotId()) && Objects.equals(getEventId(), that.getEventId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTypeSlotId(), getEventId());
  }
}
