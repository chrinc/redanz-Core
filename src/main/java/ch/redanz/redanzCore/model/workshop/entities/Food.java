package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;

@Entity
@Getter
@Setter
@Table(name = "food")
public class Food implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "food_id")
  private Long foodId;
  private String name;
  private String description;

  public Food() {
  }

  public Food(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");   put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");   put("required", "true");   put("label", "Description");}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_FOOD_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_FOOD_EN.getOutTextKey()); }});

      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(foodId));
        put("name", name);
        put("description", description);
      }
    };
  }
}
