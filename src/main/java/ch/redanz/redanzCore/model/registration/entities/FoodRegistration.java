package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
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
  private Long foodRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_food_slot_id")
  private EventFoodSlot eventFoodSlot;

  public FoodRegistration(Registration registration, EventFoodSlot eventFoodSlot) {
    this.registration = registration;
    this.eventFoodSlot = eventFoodSlot;
  }
}
