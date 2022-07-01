package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class TrackDiscountId implements Serializable {
  private Long discountId;
  private Long trackId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrackDiscountId)) return false;
    TrackDiscountId that = (TrackDiscountId) o;
    return getDiscountId().equals(that.getDiscountId()) && getTrackId().equals(that.getTrackId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDiscountId(), getTrackId());
  }
}
