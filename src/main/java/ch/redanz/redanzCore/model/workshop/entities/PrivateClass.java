package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "private_class")
public class PrivateClass implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "private_class_id")
  private Long privateClassId;
  private String name;
  private String description;
  private Integer capacity;
  private double price;

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public PrivateClass() {
  }

  public PrivateClass(
    String name,
    String description,
    Integer capacity,
    double price,
    Boolean partnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.capacity = capacity;
    this.price = price;
    this.partnerRequired = partnerRequired;
    this.requiredDanceLevel = danceLevel;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");   put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double"); put("required", "true");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "false");  put("label", "Description");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number"); put("required", "true");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "partnerRequired");        put("type", "bool");                            put("labelTrue", "Partner required"); put("labelFalse", "No Partner required"); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(privateClassId));
        put("name", name);
        put("price", String.valueOf(price));
        put("description", description);
        put("capacity", String.valueOf(capacity));
        put("partnerRequired", partnerRequired.toString());
      }
    };
  }
}
