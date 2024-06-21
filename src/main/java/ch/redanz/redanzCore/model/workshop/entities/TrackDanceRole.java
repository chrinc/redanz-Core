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
@Table(name = "track_dance_role")
public class TrackDanceRole {


  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "track_dance_role_id")
  private Long trackDanceRoleId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "track_id")
  @JsonIgnore
  private Track track;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dance_role_id")
  private DanceRole danceRole;

  private String hint;

  public TrackDanceRole() {
  }

  public TrackDanceRole(
    Track track,
    DanceRole danceRole,
    String hint
  ) {
    this.track = track;
    this.danceRole = danceRole;
    this.hint = hint;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");             put("type", "id");                                        put("label", "id");           }});
        add(new HashMap<>() {{put("key", "trackId");        put("type", "id");                                        put("label", "Track Id");          }});
        add(new HashMap<>() {{put("key", "danceRole");      put("type", "listText");    put("required", "false");   put("label", "Dance Role"); put("list", null);}});
        add(new HashMap<>() {{put("key", "hint");           put("type", "label");        put("required", "false");    put("label", "Hint");             }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_DANCE_ROLES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_DANCE_ROLE_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", trackDanceRoleId.toString());
        put("trackId", track.getTrackId().toString());
        put("danceRole", danceRole.getDanceRoleId().toString());
        put("hint", hint);
      }
    };
  }
}
