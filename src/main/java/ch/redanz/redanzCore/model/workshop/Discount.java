package ch.redanz.redanzCore.model.workshop;

import ch.redanz.redanzCore.model.registration.Registration;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="discount")
public class Discount implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="discount_id")
  private Long discountId;
  private String name;
  private Integer discount;
  private String description;

  @ManyToMany(mappedBy = "discountList")
  private List<Registration> registrationList;

  public Discount () {}

  // getter

  public Long getDiscountId() {
    return discountId;
  }

  // setter
}
