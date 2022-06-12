package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="track_dance_role")
@Slf4j
public class TrackDanceRole implements Serializable {

  @EmbeddedId
  private final TrackDanceRoleId trackDanceRoleId = new TrackDanceRoleId();

  @ManyToOne
  @MapsId("danceRoleId")
  @JoinColumn(name="dance_role_id")
  private DanceRole danceRole;

  @ManyToOne
  @JsonIgnore
  @MapsId("trackId")
  @JoinColumn(name="track_id")
  private Track track;

  public TrackDanceRole() {}

  public TrackDanceRole(DanceRole danceRole, Track track) {
    this.danceRole = danceRole;
    this.track = track;
  }

  public DanceRole getDanceRole() {
    return danceRole;
  }

  public void setDanceRole(DanceRole danceRole) {
    this.danceRole = danceRole;
  }

  public Track getTrack() {
    return track;
  }

  public void setTrack(Track track) {
    this.track = track;
  }

}

