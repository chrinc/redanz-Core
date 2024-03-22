package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
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
    this.name = name;
    this.color = color;
    this.seqNr = seqNr;
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
