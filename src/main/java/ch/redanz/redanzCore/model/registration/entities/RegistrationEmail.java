package ch.redanz.redanzCore.model.registration.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Slf4j
@ToString
@Getter
@Setter
@Table(name="registration_email")
public class RegistrationEmail implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "registration_email_id")
  private Long registrationEmailId;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "registration_id", nullable = false)
  private Registration registration;

  @Column(name = "reminder_sent_date")
  private ZonedDateTime reminderSentDate;

  @Column(name = "done_sent_date")
  private ZonedDateTime doneSentDate;

  @Column(name = "received_sent_date")
  private ZonedDateTime receivedSentDate;

  @Column(name = "released_sent_date")
  private ZonedDateTime releasedSentDate;

  @Column(name = "cancelled_sent_date")
  private ZonedDateTime cancelledSentDate;

  public RegistrationEmail() {}

  public RegistrationEmail(Registration registration) {
    this.registration = registration;
  }

  public RegistrationEmail(Registration registration, ZonedDateTime receivedSentDate) {
    this.registration = registration;
    this.receivedSentDate = receivedSentDate;
  }
}
