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
@Table(name="hostee_slot_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HosteeSlotRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "hostee_slot_registration_id")
  private Long hosteeSlotRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="hostee_registration_id")
  @JsonIgnore
  private HosteeRegistration hosteeRegistration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="hostee_slot_id")
  private Slot slot;

  public HosteeSlotRegistration(HosteeRegistration hosteeRegistration, Slot slot) {
    this.hosteeRegistration = hosteeRegistration;
    this.slot = slot;
  }
}
