package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "special")
public class Special {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "speical_id")
  private Long specialId;
  private String name;
  private String description;
//  private double price;
//  private Slot slot;

  public Special() {
  }

  public Special(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
