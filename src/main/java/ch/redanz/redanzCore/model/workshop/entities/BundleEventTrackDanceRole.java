package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "bundle_event_track_dance_role")
public class BundleEventTrackDanceRole {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "bundle_event_track_dance_role_id")
  private Long bundleEventTrackDanceRoleId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bundle_event_track_id")
  @JsonIgnore
  private BundleEventTrack bundleEventTrack;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_dance_role_id")
  private EventDanceRole eventDanceRole;

//  private String hint;

  @Column(name = "sold_out")
  private boolean soldOut;

  public BundleEventTrackDanceRole() {
  }

  public BundleEventTrackDanceRole(
    BundleEventTrack bundleEventTrack,
    EventDanceRole eventDanceRole
  ) {
    this.bundleEventTrack = bundleEventTrack;
    this.eventDanceRole = eventDanceRole;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");             put("type", "id");                                        put("label", "id");           }});
        add(new HashMap<>() {{put("key", "bundleEventTrackId"); put("type", "id");                                        put("label", "Bundle Event Track Id");          }});
        add(new HashMap<>() {{put("key", "eventDanceRole");      put("type", "listText");    put("required", "false");   put("label", "Dance Role"); put("list", null);}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_DANCE_ROLES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_DANCE_ROLE_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", bundleEventTrackDanceRoleId.toString());
        put("bundleEventTrackId", bundleEventTrack.getBundleEventTrackId().toString());
        put("eventDAnceRole", eventDanceRole.getEventDanceRoleId().toString());
//        put("hint", hint);
      }
    };
  }
}
