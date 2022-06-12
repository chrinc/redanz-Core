package ch.redanz.redanzCore.model.workshop;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="slot")
public class Slot implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name="slot_id")
  private Long slotId;
  private String name;
  private String description;

  @OneToMany(cascade=CascadeType.ALL, mappedBy = "slot")
  private List<FoodRegistration> foodRegistrationList;

  public Slot () {}

  // getter
  // setter
}
