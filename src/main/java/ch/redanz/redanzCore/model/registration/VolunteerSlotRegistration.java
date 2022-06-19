package ch.redanz.redanzCore.model.registration;

import ch.redanz.redanzCore.model.workshop.Slot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="volunteer_slot_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerSlotRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "volunteer_slot_registration_id")
  private Long volunteerSlotRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="volunteer_registration_id")
  @JsonIgnore
  private VolunteerRegistration volunteerRegistration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="volunteer_slot_id")
  private Slot slot;

  public VolunteerSlotRegistration(VolunteerRegistration volunteerRegistration, Slot slot) {
    this.volunteerRegistration = volunteerRegistration;
    this.slot = slot;
  }
}
