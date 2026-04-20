package ch.redanz.redanzCore.model.workshop.entities;


import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "event_discount")
public class EventDiscount implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_discount_id")
  private Long eventDiscountId;

  private String name;
  private String description;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @Column(name = "discount_amount")
  private double discountAmount;

  private Integer capacity;

  public EventDiscount() {
  }
  public EventDiscount(Event event, double discountAmount, Integer capacity, String name, String description) {
    this.event = event;
    this.discountAmount = discountAmount;
    this.capacity = capacity;
    this.name = name;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");  put("required", "true");      put("label", "Name");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "false");     put("label", "Description");}});

        add(new HashMap<>() {{put("key", "discountAmount");       put("type", "double");  put("required", "true");   put("label", "Discount");}});
        add(new HashMap<>() {{put("key", "eventPartInfo"); put("type", "partInfo");        put("eventPartKey", "discount");                          put("label", OutTextConfig.LABEL_DISCOUNT_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");  put("required", "false");  put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "plural");                put("type", "title");   put("label", OutTextConfig.LABEL_DISCOUNTS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");                put("type", "title");   put("label", OutTextConfig.LABEL_DISCOUNT_EN.getOutTextKey()); }});

      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventDiscountId));
        put("name", name);
        put("description", description);
        put("discount", null);
        put("discountAmount", String.valueOf(discountAmount));
        put("capacity", String.valueOf(capacity));
      }
    };
  }
}

