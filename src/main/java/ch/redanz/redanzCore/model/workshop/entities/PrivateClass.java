package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.text.NumberFormat;
import java.util.*;

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

  @Column(name = "partner_required")
  private Boolean partnerRequired;

  @Column(name = "required_dance_level")
  @Enumerated(EnumType.STRING)
  private DanceLevel requiredDanceLevel;

  public PrivateClass() {
  }

  public PrivateClass(
    String name,
    String description,
    Boolean partnerRequired,
    DanceLevel danceLevel
  ) {
    this.name = name;
    this.description = description;
    this.partnerRequired = partnerRequired;
    this.requiredDanceLevel = danceLevel;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");   put("required", "true");   put("label", "Name");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "true");   put("label", "Description");}});
        add(new HashMap<>() {{put("key", "partnerRequired");      put("type", "bool");                              put("labelTrue", "Partner required"); put("labelFalse", "No Partner required"); }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_PRIVATES_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_PRIVATE_EN.getOutTextKey()); }});
      }
    };
  }

  public static List<Map<String, String>> eventSchema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");}});
        add(new HashMap<>() {{put("key", "privateClass");         put("type", "listInfo");  put("required", "true"); put("label", "Private Classes"); put("list", null); put("infoKey", "price");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "text");   put("required", "true");   put("label", "Name");}});
//        add(new HashMap<>() {{put("eventPartKey", "price");                put("type", "double"); put("required", "true");   put("label", "Price");}});
      }
    };
  }


  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(privateClassId));
        put("name", name);
//        put("price", String.valueOf(price));
        put("description", description);
//        put("capacity", String.valueOf(capacity));
        put("partnerRequired", partnerRequired.toString());
      }
    };
  }
  public Map<String, String> eventDataMap() {
    return new HashMap<>() {
      NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("de", "CH"));
      {
        put("id", String.valueOf(privateClassId));
        put("name", description);
//        put("price", currencyFormatter.format(price));
      }
    };
  }
}
