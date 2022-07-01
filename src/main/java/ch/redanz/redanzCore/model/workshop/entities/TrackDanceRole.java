package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "track_dance_role")
public class TrackDanceRole implements Serializable {

  @EmbeddedId
  private final TrackDanceRoleId trackDanceRoleId = new TrackDanceRoleId();

  @ManyToOne
  @MapsId("danceRoleId")
  @JoinColumn(name = "dance_role_id")
  private DanceRole danceRole;

  @ManyToOne
  @JsonIgnore
  @MapsId("trackId")
  @JoinColumn(name = "track_id")
  private Track track;

  public TrackDanceRole() {
  }

  public TrackDanceRole(DanceRole danceRole, Track track) {
    this.danceRole = danceRole;
    this.track = track;
  }
}

