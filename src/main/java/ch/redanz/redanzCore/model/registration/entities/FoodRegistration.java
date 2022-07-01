package ch.redanz.redanzCore.model.registration.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "food_registration")
public class FoodRegistration {
  @EmbeddedId
  private FoodRegistrationId foodRegistrationId = new FoodRegistrationId();
}
