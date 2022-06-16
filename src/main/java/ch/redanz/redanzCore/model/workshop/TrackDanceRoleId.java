package ch.redanz.redanzCore.model.workshop;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class TrackDanceRoleId implements Serializable {

  private Long danceRoleId;
  private Long trackId;

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
