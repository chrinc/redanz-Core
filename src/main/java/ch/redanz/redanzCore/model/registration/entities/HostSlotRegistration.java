package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
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
@Table(name = "host_slot_registration")
public class HostSlotRegistration implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "host_slot_registration_id")
  private Long hostSlotRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "host_registration_id")
  @JsonIgnore
  private HostRegistration hostRegistration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "host_slot_id")
  private EventSlot eventSlot;

  public HostSlotRegistration(HostRegistration hostRegistration, EventSlot eventSlot) {
    this.hostRegistration = hostRegistration;
    this.eventSlot = eventSlot;
  }
}
