package ch.redanz.redanzCore.model.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="dance_role")
@Slf4j
public class DanceRole {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name="dance_role_id")
  private Long danceRoleId;
  private String name;
  private String description;


  @OneToMany(cascade=CascadeType.ALL, mappedBy = "danceRole")
  @JsonIgnore
  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  public DanceRole() {}

  public Long getDanceRoleId() {
    return danceRoleId;
  }

  public void setDanceRoleId(Long danceRoleId) {
    this.danceRoleId = danceRoleId;
  }

  public List<TrackDanceRole> getTrackDanceRoles() {
    return trackDanceRoles;
  }

  public void setTrackDanceRoles(List<TrackDanceRole> trackDanceRoles) {
    this.trackDanceRoles = trackDanceRoles;
  }

  public DanceRole(String name, String description) {
    this.name = name;
    this.description = description;
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

}

