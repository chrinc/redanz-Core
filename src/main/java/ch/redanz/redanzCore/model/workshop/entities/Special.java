package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "special")
public class Special implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "special_id")
  private Long specialId;
  private String name;
  private String description;
  private String url;
  private double price;
  private Integer capacity;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public Special() {
  }

  public Special(String name, String description, Double price, Integer capacity, boolean soldOut, String url) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.capacity = capacity;
    this.soldOut = soldOut;
    this.url = url;
  }
  public Special(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");  put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double"); put("required", "true");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "false");  put("label", "Description");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number"); put("required", "true");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "url");                  put("type", "label");  put("required", "false");  put("label", "url");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(specialId));
        put("name", name);
        put("price", String.valueOf(price));
        put("description", description);
        put("capacity", String.valueOf(capacity));
        put("url", url);
      }
    };
  }
}
