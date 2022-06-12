package ch.redanz.redanzCore.model.workshop;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TrackDanceRoleId implements Serializable {

  private Long danceRoleId;
  private Long trackId;

  public Long getDanceRoleId() {
    return danceRoleId;
  }
  public void setDanceRoleId(Long danceRoleId) {
    this.danceRoleId = danceRoleId;
  }
  public Long getTrackId() {
    return trackId;
  }
  public void setTrackId(Long trackId) {
    this.trackId = trackId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof TrackDanceRoleId)) return false;
    TrackDanceRoleId that = (TrackDanceRoleId) o;
    return getDanceRoleId().equals(that.getDanceRoleId()) &&
      getTrackId().equals(that.getTrackId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getDanceRoleId(), getTrackId());
  }
}
