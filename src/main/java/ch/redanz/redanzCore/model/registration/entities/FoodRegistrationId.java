package ch.redanz.redanzCore.model.registration.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FoodRegistrationId implements Serializable {

  @JsonIgnore
  @Column(name = "registration_id")
  private long registrationId;

  @Column(name = "food_id")
  private long foodId;

  @Column(name = "slot_id")
  private long slotId;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof FoodRegistrationId)) return false;
    FoodRegistrationId that = (FoodRegistrationId) o;
    return getRegistrationId() == that.getRegistrationId() &&
      getFoodId() == that.getFoodId() &&
      getSlotId() == that.getSlotId();
  }

  @Override
  public int hashCode() {
    return Objects.hash(getRegistrationId(), getFoodId(), getSlotId());
  }
}
