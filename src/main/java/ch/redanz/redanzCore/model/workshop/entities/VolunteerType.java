package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "volunteer_type")
public class VolunteerType implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_type_id")
  private Long volunteerTypeId;
  private String name;
  private String description;

  public VolunteerType() {
  }
  public VolunteerType(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{ put("key", "id");              put("type", "id");                               put("label", "Volunteer Type Id"); }});
        add(new HashMap<>() {{ put("key", "name");            put("type", "label");   put("required", "true");  put("label", "Name");     }});
        add(new HashMap<>() {{ put("key", "description");     put("type", "label");   put("required", "false");  put("label", "Description"); }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_VOLUNTEER_TYPES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_VOLUNTEER_TYPE_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(volunteerTypeId));
        put("name", name);
        put("description", description);
      }
    };
  }
}
