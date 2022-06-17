package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.model.workshop.Food;
import ch.redanz.redanzCore.model.workshop.OutTextId;
import ch.redanz.redanzCore.model.workshop.Slot;
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
@Table(name="food_registration")
public class FoodRegistration {
  @EmbeddedId
  private FoodRegistrationId foodRegistrationId = new FoodRegistrationId();

  @MapsId("registrationId")
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="registration_id")
  private Registration registration;

  @MapsId("slotId")
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="slot_id")
  private Slot slot;

  @MapsId("foodId")
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="food_id")
  private Food food;
}
