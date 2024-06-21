package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter

@Table(name = "slot")
public class Slot implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "slot_id")
  private Long slotId;

  @Column(name = "seq_nr")
  private Integer seqNr;

  private String name;
  private String color;

  public Slot() {
  }

  public Slot(String name, String color, Integer seqNr) {
    this.name  = name;
    this.color = color;
    this.seqNr = seqNr;
  }

  public static List<Map<String, String>> schema() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{ put("key", "id");              put("type", "id");                               put("label", "Track Id"); }});
        add(new HashMap<>() {{ put("key", "name");            put("type", "label");   put("required", "true");  put("label", "Name");     }});
        add(new HashMap<>() {{ put("key", "color");           put("type", "color");  put("required", "false");  put("label", "Color"); }});
        add(new HashMap<>() {{put("key", "plural");              put("type", "title");           put("label", OutTextConfig.LABEL_SLOTS_EN.getOutTextKey()); }});
        add(new HashMap<>() {{put("key", "singular");            put("type", "title");         put("label", OutTextConfig.LABEL_SLOT_EN.getOutTextKey()); }});
      }
    };
  }

  public Map<String, String> dataMap() {
    return new HashMap<>() {
      {
        put("id", String.valueOf(slotId));
        put("name", name);
        put("color", color);
      }
    };
  }
}
