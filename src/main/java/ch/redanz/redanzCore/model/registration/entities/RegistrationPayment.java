package ch.redanz.redanzCore.model.registration.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Slf4j
@ToString
@Getter
@Setter
@NoArgsConstructor
@Table(name="registration_payment")
public class RegistrationPayment implements Serializable{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "registration_payment_id")
  private Long registrationPaymentId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id", nullable = false)
  private Registration registration;

  @Column(name = "received_date")
  LocalDateTime transactionDate;

  @Column(name = "amount")
  Long amount;

  public RegistrationPayment(Registration registration, Long amount, LocalDateTime transactionDate) {
    this.registration = registration;
    this.amount = amount;
    this.transactionDate = transactionDate;
  }

}
