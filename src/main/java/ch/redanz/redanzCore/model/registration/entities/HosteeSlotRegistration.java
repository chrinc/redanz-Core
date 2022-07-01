package ch.redanz.redanzCore.model.registration.entities;


import ch.redanz.redanzCore.model.workshop.entities.Slot;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hostee_slot_registration")
public class HosteeSlotRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "hostee_slot_registration_id")
  private Long hosteeSlotRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "hostee_registration_id")
  @JsonIgnore
  private HosteeRegistration hosteeRegistration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "hostee_slot_id")
  private Slot slot;

  public HosteeSlotRegistration(HosteeRegistration hosteeRegistration, Slot slot) {
    this.hosteeRegistration = hosteeRegistration;
    this.slot = slot;
  }
}
