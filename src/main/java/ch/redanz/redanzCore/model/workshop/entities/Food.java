package ch.redanz.redanzCore.model.workshop.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Table(name = "food")
public class Food implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "food_id")
  private Long foodId;
  private String name;
  private double price;
  private String description;

  public Food() {
  }

  public Food(String name, double price, String description) {
    this.name = name;
    this.price = price;
    this.description = description;
  }
}
