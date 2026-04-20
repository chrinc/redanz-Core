package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.EventSpecial;
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
@Table(name = "special_registration")
public class SpecialRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "special_registration_id")
  private Long specialRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "event_special_id")
  private EventSpecial eventSpecial;

  public SpecialRegistration(Registration registration, EventSpecial eventSpecial) {
    this.registration = registration;
    this.eventSpecial = eventSpecial;
  }
}
