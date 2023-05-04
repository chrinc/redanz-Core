package ch.redanz.redanzCore.model.registration.entities;


import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
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
@Table(name = "private_class_registration")
public class PrivateClassRegistration {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "private_class_registration_id")
  private Long privateClassRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "private_class_id")
  private PrivateClass privateClass;

  public PrivateClassRegistration(Registration registration, PrivateClass privateClass) {
    this.registration = registration;
    this.privateClass = privateClass;
  }
}
