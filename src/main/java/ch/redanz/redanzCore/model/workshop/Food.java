package ch.redanz.redanzCore.model.workshop;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="food")
public class Food implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="food_id")
  private Long foodId;
  private String name;
  private Integer price;
  private String description;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "food")
  private List<FoodRegistration> foodRegistrationList;

  public Food () {}

  // getter
  // setter
}
