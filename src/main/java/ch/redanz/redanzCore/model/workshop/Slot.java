package ch.redanz.redanzCore.model.workshop;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Setter
@Getter

@Table(name="slot")
public class Slot implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="slot_id")
  private Long slotId;

  private String name;
  public Slot () {}

  public Slot(String name) {
    this.name = name;
  }
}
