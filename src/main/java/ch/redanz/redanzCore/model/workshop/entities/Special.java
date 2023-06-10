package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "special")
public class Special implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "special_id")
  private Long specialId;
  private String name;
  private String description;
  private double price;
  private Integer capacity;

  @Column(name = "sold_out")
  private Boolean soldOut;

  public Special() {
  }

  public Special(String name, String description, Double price) {
    this.name = name;
    this.description = description;
    this.price = price;
  }
  public Special(String name, String description) {
    this.name = name;
    this.description = description;
  }
}
