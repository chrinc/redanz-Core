package ch.redanz.redanzCore.model.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
  private double discount;
  private String description;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "discount")
  @JsonIgnore
  private List<TrackDiscount> trackDiscounts = new ArrayList<>();

  public Discount(String name, double discount, String description) {
    this.name = name;
    this.discount = discount;
    this.description = description;
  }
}
