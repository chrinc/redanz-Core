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
@Table(name = "event_special")
public class EventSpecial {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "event_special_id")
  private Long eventSpecialId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id")
  @JsonIgnore
  private Event event;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "special_id")
  private Special special;

  private double price;

  private Integer capacity;

  private String url;

  private Boolean infoOnly;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public EventSpecial() {
  }

  public EventSpecial(Special special, Event event, double price, boolean soldOut, int capacity, String url, Boolean infoOnly) {
    this.event = event;
    this.special = special;
    this.price = price;
    this.soldOut = soldOut;
    this.capacity = capacity;
    this.url = url;
    this.infoOnly = infoOnly;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                 put("label", "id");}});
        add(new HashMap<>() {{put("key", "special");              put("type", "list");    put("required", "true");   put("label", "Special"); put("list", null);}});
        add(new HashMap<>() {{put("key", "price");                put("type", "double");  put("required", "false");   put("label", "Price");}});
        add(new HashMap<>() {{put("key", "capacity");             put("type", "number");  put("required", "true");   put("label", "Capacity");}});
        add(new HashMap<>() {{put("key", "url");                  put("type", "label");   put("required", "false");  put("label", "Web Url");}});
        add(new HashMap<>() {{put("key", "eventPartInfo"); put("type", "partInfo");        put("eventPartKey", "special");                          put("label", OutTextConfig.LABEL_SPECIAL_INFO_EN.getOutTextKey());}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_SPECIALS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_SPECIAL_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "infoOnly");            put("type", "bool");;  put("required", "false");    put("labelTrue", "Info Only");            put("labelFalse", "Allow Registration");;}});

      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(eventSpecialId));
        put("special", null);
        put("price", String.valueOf(price));
        put("capacity", String.valueOf(capacity));
        put("url", url);
        put("infoOnly", String.valueOf(infoOnly));
      }
    };
  }

}
