package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
    name = "track_event_discount",
    joinColumns = @JoinColumn(name = "track_id"),
    inverseJoinColumns = @JoinColumn(name = "event_discount_id"))
  private List<EventDiscount> eventDiscounts;

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name="own_partner_required")
  private Boolean ownPartnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  public Track() {
  }

  public Track(
    String name,
    String description,
    Boolean partnerRequired,
    Boolean ownPartnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.partnerRequired = partnerRequired;
    this.ownPartnerRequired = ownPartnerRequired;
    this.requiredDanceLevel = danceLevel;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{ put("key", "eventId");            put("type", "id");                                              put("label", "BundleId"); }});
        add(new HashMap<>() {{ put("key", "id");                 put("type", "id");                                              put("label", "Track Id"); }});
        add(new HashMap<>() {{ put("key", "name");               put("type", "text");             put("required", "true");       put("label", "Name");     }});
        add(new HashMap<>() {{ put("key", "partnerRequired");    put("type", "bool");             put("required", "true");       put("labelTrue", "Partner required");     put("labelFalse", "No Partner Required"); }});
        add(new HashMap<>() {{ put("key", "ownPartnerRequired"); put("type", "bool");             put("required", "true");       put("labelTrue", "Own Partner required"); put("labelFalse", "No Own Partner Required"); }});
        add(new HashMap<>() {{ put("key", "description");        put("type", "label");            put("required", "false");      put("label", "Description"); }});
        add(new HashMap<>() {{ put("key", "discounts");          put("type", "multiselect");      put("required", "false");      put("label", "Discounts");                put("list", null);}});
        add(new HashMap<>() {{put("key", "eventPartInfo");       put("type", "partInfo");         put("eventPartKey", "track"); put("label", OutTextConfig.LABEL_TRACK_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "clone");               put("type", "action");           put("show", "true");}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_TRACKS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_TRACK_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", Long.toString(trackId));
        put("name", name);
//        put("capacity", null);
        put("partnerRequired", String.valueOf(partnerRequired));
        put("ownPartnerRequired", String.valueOf(ownPartnerRequired));
        put("description", description);
        put("discounts", null);
      }
    };
  }
  public Map<String, String> simpleDataMap() {
    return new HashMap<>() {
      {
        put("id", Long.toString(trackId));
        put("name", name);
      }
    };
  }
}

