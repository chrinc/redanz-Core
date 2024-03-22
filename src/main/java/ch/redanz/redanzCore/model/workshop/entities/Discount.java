package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "discount")
public class Discount implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "discount_id")
  private Long discountId;
  private String name;
  private double discount;
  private Integer capacity;
  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "discount")
  @JsonIgnore
  private List<TrackDiscount> trackDiscounts = new ArrayList<>();

  public Discount(String name, double discount, String description, Integer capacity) {
    this.name = name;
    this.discount = discount;
    this.description = description;
    this.capacity = capacity;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                   put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");  put("required", "true");      put("label", "Name");}});
        add(new HashMap<>() {{put("key", "discount");             put("type", "double"); put("required", "true");      put("label", "Discount");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "false");     put("label", "Description");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number"); put("required", "false");     put("label", "Capacity");}});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(discountId));
        put("name", name);
        put("discount", String.valueOf(discount));
        put("description", description);
        put("capacity", String.valueOf(capacity));
      }
    };
  }
}
