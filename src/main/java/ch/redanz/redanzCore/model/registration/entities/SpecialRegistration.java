package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.Special;
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
  @JoinColumn(name = "special_id")
  private Special specialId;

  public SpecialRegistration(Registration registration, Special specialId) {
    this.registration = registration;
    this.specialId = specialId;
  }
}
