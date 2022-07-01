package ch.redanz.redanzCore.model.registration.entities;

import ch.redanz.redanzCore.model.workshop.entities.Discount;
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
@Table(name = "discount_registration")
public class DiscountRegistration {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "discount_registration_id")
  private Long discountRegistrationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id")
  @JsonIgnore
  private Registration registration;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "discount_id")
  private Discount discount;

  public DiscountRegistration(Registration registration, Discount discount) {
    this.registration = registration;
    this.discount = discount;
  }
}
