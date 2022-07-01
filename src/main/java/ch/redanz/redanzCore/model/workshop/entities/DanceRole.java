package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Slf4j
@Getter
@Setter
@Table(name = "dance_role")
public class DanceRole {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "dance_role_id")
  private Long danceRoleId;
  private String name;
  private String description;


  @OneToMany(cascade = CascadeType.ALL, mappedBy = "danceRole")
  @JsonIgnore
  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  public DanceRole() {
  }

  public DanceRole(String name, String description) {
    this.name = name;
    this.description = description;
  }
}

