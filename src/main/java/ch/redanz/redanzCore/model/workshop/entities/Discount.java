package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
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
  private String description;

  public Discount(String name
    , String description
  ) {
    this.name = name;
    this.description = description;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("key", "id");                   put("type", "id");                                   put("label", "id");}});
        add(new HashMap<>() {{put("key", "name");                 put("type", "label");  put("required", "true");      put("label", "Name");}});
        add(new HashMap<>() {{put("key", "description");          put("type", "label");  put("required", "false");     put("label", "Description");}});
//        add(new HashMap<>() {{put("key", "eventPartInfo");        put("type", "partInfo");        put("eventPartKey", "discount");                          put("label", "Bundle Info");}});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_DISCOUNTS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_DISCOUNT_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(discountId));
        put("name", name);
        put("description", description);
      }
    };
  }
}
