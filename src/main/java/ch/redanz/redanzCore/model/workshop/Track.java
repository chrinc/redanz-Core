package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="track")
@Slf4j
public class Track implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="track_id")
  private Long trackId;

  private String name;
  private String description;
  private Integer capacity;

  @Column(name="partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "track")
  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  @JsonIgnore
  @OneToMany(cascade=CascadeType.ALL, mappedBy = "track")
  private List<BundleTrack> bundleTracks = new ArrayList<>();

  public Track () {}

  public Track(
    String name,
    String description,
    Integer capacity,
    Boolean partnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.capacity = capacity;
    this.partnerRequired = partnerRequired;
    this.requiredDanceLevel = danceLevel;
  }

  public Long getTrackId() {
    return trackId;
  }

  public void setTrackId(Long trackId) {
    this.trackId = trackId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Boolean getPartnerRequired() {
    return partnerRequired;
  }

  public void setPartnerRequired(Boolean partnerRequired) {
    this.partnerRequired = partnerRequired;
  }

  public DanceLevel getRequiredDanceLevel() {
    return requiredDanceLevel;
  }

  public void setRequiredDanceLevel(DanceLevel requiredDanceLevel) {
    this.requiredDanceLevel = requiredDanceLevel;
  }

  public List<BundleTrack> getBundleTracks() {
    return bundleTracks;
  }

  public void setBundleTracks(List<BundleTrack> bundleTracks) {
    this.bundleTracks = bundleTracks;
  }

  public List<TrackDanceRole> getTrackDanceRoles() {
    return trackDanceRoles;
  }

  public void setTrackDanceRoles(List<TrackDanceRole> trackDanceRoles) {
    this.trackDanceRoles = trackDanceRoles;
  }

}

