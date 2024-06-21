package ch.redanz.redanzCore.model.workshop.entities;

import lombok.*;
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
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "event_part")
public class EventPart {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_part_id")
  private Long eventPartId;

  private String name;

  @Column(name = "event_part_key")
  private String eventPartKey;

  public EventPart(
    String eventPartKey,
    String name
  ) {
    this.eventPartKey = eventPartKey;
    this.name = name;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{
          put("key", "id");
          put("type", "id");
          put("label", "id");
        }});
        add(new HashMap<>() {{
          put("key", "name");
          put("type", "label");
          put("required", "true");
          put("label", "Name");
        }});
        add(new HashMap<>() {{
          put("key", "description");
          put("type", "label");
          put("required", "false");
          put("label", "Description");
        }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventPartId));
        put("key", eventPartKey);
        put("name", name);
      }
    };
  }
}
