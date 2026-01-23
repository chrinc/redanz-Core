package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_part_info")
public class EventPartInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_part_info_id")
  private Long eventPartInfoId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_part_id")
  private EventPart eventPart;

  private String title;

  @Column(name = "title_active")
  private boolean titleActive;

  @Column(name = "title_exist")
  private String titleExist;

  @Column(name = "title_exist_active")
  private boolean titleExistActive;

  private String invalid;

  @Column(name = "invalid_active")
  private boolean invalidActive;

  private String subtitle;
  @Column(name = "subtitle_active")
  private boolean subtitleActive;

  @Column(name = "subtitle_link")
  private String subtitleLink;

  @Column(name = "subtitle_link_active")
  private boolean subtitleLinkActive;

  private String hint;

  @Column(name = "hint_active")
  private boolean hintActive;

  @Column(name = "hint_link")
  private String hintLink;

  @Column(name = "hint_link_active")
  private boolean hintLinkActive;

  private String hint2;
  @Column(name = "hint2_active")
  private boolean hint2Active;

  @Column(name = "hint2_link")
  private String hint2Link;

  @Column(name = "hint2_link_active")
  private boolean hint2LinkActive;

  public EventPartInfo(
    Event event,
    EventPart eventPart,
    Boolean titleActive,
    Boolean titleExistActive,
    Boolean invalidActive,
    Boolean subtitleActive,
    Boolean subtitleLinkActive,
    Boolean hintActive,
    Boolean hintLinkActive,
    Boolean hint2Active,
    Boolean hint2LinkActive
  ) {
    this.event = event;
    this.eventPart = eventPart;
    this.titleActive = titleActive;
    this.titleExistActive = titleExistActive;
    this.invalidActive = invalidActive;
    this.subtitleActive = subtitleActive;
    this.subtitleLinkActive = subtitleLinkActive;
    this.hintActive = hintActive;
    this.hintLinkActive = hintLinkActive;
    this.hint2Active = hint2Active;
    this.hint2LinkActive = hint2LinkActive;
  }

  public EventPartInfo(
    Event event,
    EventPart eventPart,
    String title,
    Boolean titleActive,
    String titleExist,
    Boolean titleExistActive,
    String invalid,
    Boolean invalidActive,
    String subtitle,
    Boolean subtitleActive,
    String subtitleLink,
    Boolean subtitleLinkActive,
    String hint,
    Boolean hintActive,
    String hintLink,
    Boolean hintLinkActive,
    String hint2,
    Boolean hint2Active,
    String hint2Link,
    Boolean hint2LinkActive
  ) {
    this.event = event;
    this.eventPart = eventPart;
    this.title = title;
    this.titleActive = titleActive;
    this.titleExist = titleExist;
    this.titleExistActive = titleExistActive;
    this.invalid = invalid;
    this.invalidActive = invalidActive;
    this.subtitle = subtitle;
    this.subtitleActive = subtitleActive;
    this.subtitleLink = subtitleLink;
    this.subtitleLinkActive = subtitleLinkActive;
    this.hint = hint;
    this.hintActive = hintActive;
    this.hintLink = hintLink;
    this.hintLinkActive = hintLinkActive;
    this.hint2 = hint2;
    this.hint2Active = hint2Active;
    this.hint2Link = hint2Link;
    this.hint2LinkActive = hint2LinkActive;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");             put("type", "id");                                        put("label", "EventId");           }});
        add(new HashMap<>() {{put("key", "eventId");        put("type", "id");                                        put("label", "Event Id");          }});
        add(new HashMap<>() {{put("key", "title");          put("type", "label");        put("required", "true");     put("label", "Title");             }});
        add(new HashMap<>() {{put("key", "titleExist");     put("type", "label");        put("required", "true");     put("label", "Title Existing Registration");             }});
        add(new HashMap<>() {{put("key", "invalid");        put("type", "label");        put("required", "true");     put("label", "Invalid Selection");             }});
        add(new HashMap<>() {{put("key", "subtitle");       put("type", "label");        put("required", "true");    put("label", "Subtitle");         }});
        add(new HashMap<>() {{put("key", "subtitleLink");   put("type", "label");        put("required", "true");    put("label", "Subtitle Link");         }});
        add(new HashMap<>() {{put("key", "hint");           put("type", "label");        put("required", "true");    put("label", "Hint");             }});
        add(new HashMap<>() {{put("key", "hintLink");       put("type", "label");        put("required", "true");    put("label", "Hint Link");             }});
        add(new HashMap<>() {{put("key", "hint2");          put("type", "label");        put("required", "true");    put("label", "Hint Two Hint");             }});
        add(new HashMap<>() {{put("key", "hint2Link");      put("type", "label");        put("required", "true");    put("label", "Hint Two Link");             }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", ""); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", ""); }});
        add(new HashMap<>() {{put("key", "count");          put("type", "single");}});
        add(new HashMap<>() {{put("key", "delete");         put("type", "action");      put("hide", "true");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", eventPartInfoId.toString());
        put("eventId", event.getEventId().toString());
        put("eventPart", eventPart.toString());
        put("title", title);
        put("titleExist", titleExist);
        put("invalid", invalid);
        put("subtitle", subtitle);
        put("subtitleLink", subtitleLink);
        put("hint", hint);
        put("hintLink", hintLink);
        put("hint2", hint2);
        put("hint2Link", hint2Link);
      }
    };
  }
}
