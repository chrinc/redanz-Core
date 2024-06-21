package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Slf4j
@Table(name = "event_private_class")
public class EventPrivateClass {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_private_class_id")
  private Long eventPrivateClassId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "private_class_id")
//  @JsonIgnore
  private PrivateClass privateClass;

  private double price;

  private Integer capacity;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public EventPrivateClass() {
  }

  public EventPrivateClass(PrivateClass privateClass, Event event, double price, boolean soldOut, int capacity) {
    this.event = event;
    this.privateClass = privateClass;
    this.price = price;
    this.soldOut = soldOut;
    this.capacity = capacity;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "privateClass");         put("type", "list");    put("required", "true");   put("label", "Private Class"); put("list", null);}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double");  put("required", "true");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");  put("required", "true");      put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "private");                          put("label", OutTextConfig.LABEL_PRIVATE_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "plural");               put("type", "title");           put("label", OutTextConfig.LABEL_PRIVATES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");             put("type", "title");         put("label", OutTextConfig.LABEL_PRIVATE_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventPrivateClassId));
        put("privateClass", null);
        put("price", String.valueOf(price));
        put("capacity", String.valueOf(capacity));
      }
    };
  }

}
