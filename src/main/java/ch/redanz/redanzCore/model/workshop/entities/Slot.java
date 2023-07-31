package ch.redanz.redanzCore.model.workshop.entities;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Setter
@Getter

@Table(name = "slot")
public class Slot implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "slot_id")
  private Long slotId;

  private String name;

  public Slot() {
  }

  public Slot(String name) {
    this.name = name;
  }
}
