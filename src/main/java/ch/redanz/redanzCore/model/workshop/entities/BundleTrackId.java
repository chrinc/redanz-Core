package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class BundleTrackId implements Serializable {

  private Long trackId;
  private Long bundleId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BundleTrackId)) return false;
    BundleTrackId that = (BundleTrackId) o;
    return trackId.equals(that.trackId) &&
      bundleId.equals(that.bundleId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(trackId, bundleId);
  }
}
