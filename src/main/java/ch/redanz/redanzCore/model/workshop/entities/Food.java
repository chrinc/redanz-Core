package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
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
@Table(name = "food")
public class Food implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "food_id")
  private Long foodId;
  private String name;
  private double price;
  private String description;

  public Food() {
  }

  public Food(String name, double price, String description) {
    this.name = name;
    this.price = price;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");   put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double");  put("required", "true");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");   put("required", "true");   put("label", "Description");}});
        add(new HashMap<>() {{put("key", "slot");                 put("type", "list");    put("required", "true");   put("label", "Slot"); put("list", null);}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(foodId));
        put("name", name);
        put("price", String.valueOf(price));
        put("description", description);
        put("slot", null);
      }
    };
  }
}
