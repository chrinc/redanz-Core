package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "track")
public class Track implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "track_id")
  private Long trackId;
  private String name;
  private String description;
  private Integer capacity;

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
  private List<TrackDanceRole> trackDanceRoles = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "track")
  private List<TrackDiscount> trackDiscounts = new ArrayList<>();

  @Column(name = "sold_out")
  private boolean soldOut;

  public Track() {
  }

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

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{ put("key", "bundleId");        put("type", "id");                                          put("label", "BundleId"); }});
        add(new HashMap<>() {{ put("key", "id");              put("type", "id");                                          put("label", "Track Id"); }});
        add(new HashMap<>() {{ put("key", "name");            put("type", "text");             put("required", "true");   put("label", "Name");     }});
        add(new HashMap<>() {{ put("key", "capacity");        put("type", "number");           put("required", "true");   put("label", "Capacity"); }});
        add(new HashMap<>() {{ put("key", "partnerRequired"); put("type", "bool");             put("required", "true");   put("labelTrue", "Partner required"); put("labelFalse", "No Partner Required"); }});
        add(new HashMap<>() {{ put("key", "description");     put("type", "text");             put("required", "false");  put("label", "Description"); }});
        add(new HashMap<>() {{ put("key", "discounts");       put("type", "multiselect");      put("required", "false");  put("label", "Discounts");           put("list", null);}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", Long.toString(trackId));
        put("name", name);
        put("capacity", String.valueOf(capacity));
        put("partnerRequired", String.valueOf(partnerRequired));
        put("description", description);
        put("discounts", null);
      }
    };
  }
}

