package ch.redanz.redanzCore.model.workshop;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class BundleTrackId implements Serializable {

  private Long trackId;
  private Long bundleId;

  public Long getTrackId() {
    return trackId;
  }
  public void setTrackId(Long trackId) {
    this.trackId = trackId;
  }
  public Long getBundleId() {
    return bundleId;
  }
  public void setBundleId(Long bundleId) {
    this.bundleId = bundleId;
  }

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