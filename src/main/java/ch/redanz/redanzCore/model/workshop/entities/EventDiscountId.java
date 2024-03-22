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
public class EventDiscountId implements Serializable {
  private Long discountId;
  private Long eventId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EventDiscountId)) return false;
    EventDiscountId that = (EventDiscountId) o;
    return Objects.equals(discountId, that.discountId) && Objects.equals(eventId, that.eventId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(discountId, eventId);
  }
}
