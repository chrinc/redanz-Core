package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private String abbreviation;

  @Column(name = "internal_id")
  private String internalId;

//
//  @OneToMany(cascade = CascadeType.ALL, mappedBy = "danceRole")
//  @JsonIgnore
//  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  public DanceRole() {
  }

  public DanceRole(String name, String description, String internalId, String abbreviation) {
    this.name = name;
    this.description = description;
    this.internalId = internalId;
    this.abbreviation = abbreviation;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                   put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");  put("required", "true");      put("label", "Name");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(danceRoleId));
        put("name", name);
      }
    };
  }
}

