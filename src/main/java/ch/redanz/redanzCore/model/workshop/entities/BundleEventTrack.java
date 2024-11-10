package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "bundle_event_track")
public class BundleEventTrack {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bundle_event_track_id")
  private Long bundleEventTrackId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_id")
  @JsonIgnore
  private Bundle bundle;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_track_id")
  private EventTrack eventTrack;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "bundleEventTrack", fetch = FetchType.EAGER)
  private Set<BundleEventTrackDanceRole> bundleEventTrackDanceRoles;

  @Column(name = "sold_out")
  private boolean soldOut;

  private int capacity;

  private String color;

  public BundleEventTrack() {
  }

  public BundleEventTrack(
    Bundle bundle,
    EventTrack eventTrack,
    int capacity
  ) {
    this.bundle = bundle;
    this.eventTrack = eventTrack;
    this.capacity = capacity;
  }

  public BundleEventTrack(
    Bundle bundle,
    EventTrack eventTrack
  ) {
    this.bundle = bundle;
    this.eventTrack = eventTrack;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                  put("type", "id");                                        put("label", "id");           }});
        add(new HashMap<>() {{put("key", "bundleId");            put("type", "id");                                        put("label", "Track Id");          }});
        add(new HashMap<>() {{put("key", "eventTrack");          put("type", "listText");    put("required", "false");   put("label", "Track"); put("list", null);}});
        add(new HashMap<>() {{put("key", "color");               put("type", "color");       put("required", "false"); put("label", "Wrist Band Color"); }});
        add(new HashMap<>() {{put("key", "bundleEventTrackDanceRoles");          put("type", "multiselectText");    put("required", "false");   put("label", "Dance Roles"); put("list", null);}});
        add(new HashMap<>() {{put("key", "capacity");            put("type", "number");       put("required", "true");    put("label", "Capacity");             }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_TRACKS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_TRACK_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", bundleEventTrackId.toString());
        put("bundleId", bundle.getBundleId().toString());
        put("eventTrack", eventTrack.getEventTrackId().toString());
        put("color", color);
        put("capacity", String.valueOf(capacity));
      }
    };
  }
}
