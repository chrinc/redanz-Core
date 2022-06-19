package ch.redanz.redanzCore.model.registration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="donation_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonationRegistration implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "donation_registration_id")
  private Long donationRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="registration_id")
  @JsonIgnore
  private Registration registration;

  private Integer amount;

  public DonationRegistration(Registration registration, Integer amount) {
    this.registration = registration;
    this.amount = amount;
  }
}
