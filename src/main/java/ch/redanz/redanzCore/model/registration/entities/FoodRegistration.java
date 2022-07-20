package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_registration")
public class FoodRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "food_registration_id")
  private Long discountRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "food_id")
  private Food food;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "slot_id")
  private Slot slot;

  public FoodRegistration(Registration registration, Food food, Slot slot) {
    this.registration = registration;
    this.food = food;
    this.slot = slot;
  }
}
