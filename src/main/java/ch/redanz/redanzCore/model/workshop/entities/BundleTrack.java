package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "bundle_track")
@Slf4j
public class BundleTrack implements Serializable {

  @EmbeddedId
  private final BundleTrackId bundleTrackId = new BundleTrackId();

  @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
  @MapsId("bundleId")
  @JsonIgnore
  @JoinColumn(name = "bundle_id")
  private Bundle bundle;

  @ManyToOne
  @MapsId("trackId")
  @JoinColumn(name = "track_id")
  private Track track;

  public BundleTrack() {
  }

  public BundleTrack(Track track, Bundle bundle) {
    this.track = track;
    this.bundle = bundle;
  }

  public Track getTrack() {
    return track;
  }

  public void setTrack(Track track) {
    this.track = track;
  }

  public Bundle getBundle() {
    return bundle;
  }

  public void setBundle(Bundle bundle) {
    this.bundle = bundle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BundleTrack)) return false;
    BundleTrack that = (BundleTrack) o;
    return getTrack().equals(that.getTrack()) &&
      getBundle().equals(that.getBundle());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getTrack(), getBundle());
  }
}

