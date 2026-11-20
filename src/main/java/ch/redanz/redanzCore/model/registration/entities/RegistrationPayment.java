package ch.redanz.redanzCore.model.registration.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import jakarta.persistence.*;
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

  // several bookings per registration (payments, refunds, fees)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id", nullable = false)
  private Registration registration;

  @Column(name = "received_date")
  LocalDateTime transactionDate;

  @Column(name = "amount")
  Long amount;

  // null: payment (bookings before refunds and fees existed)
  @Enumerated(EnumType.STRING)
  @Column(name = "type")
  PaymentType type;

  // out text key of the optional comment (EN/GE), shown instead of the default label
  @Column(name = "comment")
  String comment;

  public RegistrationPayment(Registration registration, Long amount, LocalDateTime transactionDate) {
    this(registration, amount, transactionDate, PaymentType.PAYMENT, null);
  }

  public RegistrationPayment(Registration registration, Long amount, LocalDateTime transactionDate, PaymentType type, String comment) {
    this.registration = registration;
    this.amount = amount;
    this.transactionDate = transactionDate;
    this.type = type;
    this.comment = comment;
  }

  public PaymentType getType() {
    return type == null ? PaymentType.PAYMENT : type;
  }

}
