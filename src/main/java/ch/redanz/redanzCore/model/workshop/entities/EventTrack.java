package ch.redanz.redanzCore.model.workshop.entities;

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
@Table(name = "event_track")
public class EventTrack {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_track_id")
  private Long eventTrackId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "track_id")
  private Track track;

  public EventTrack() {
  }

  public EventTrack(Track track, Event event) {
    this.event = event;
    this.track = track;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{ put("key", "track");  put("type", "multiselectText");        put("required", "false");      put("label", "Track");  put("list", null);}});

        //        add(new HashMap<>() {{put("eventPartKey", "price");                put("type", "double");  put("required", "false");   put("label", "Price");}});
//        add(new HashMap<>() {{put("eventPartKey", "capacity");             put("type", "number");  put("required", "true");   put("label", "Capacity");}});
//        add(new HashMap<>() {{put("eventPartKey", "url");                  put("type", "label");   put("required", "false");  put("label", "Web Url");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventTrackId));
        put("track", null);
//        put("price", String.valueOf(price));
//        put("url", url);
      }
    };
  }

}
