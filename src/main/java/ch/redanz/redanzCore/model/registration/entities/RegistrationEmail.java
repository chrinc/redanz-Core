package ch.redanz.redanzCore.model.registration.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
  private Instant reminderSentDate;

  @Column(name = "reminder_sent_date_tz")
  private String reminderSentDateTz;

  @Column(name = "done_sent_date")
  private Instant doneSentDate;

  @Column(name = "done_sent_date_tz")
  private String doneSentDateTz;

  @Column(name = "received_sent_date")
  private Instant receivedSentDate;

  @Column(name = "received_sent_date_tz")
  private String receivedSentDateTz;

  @Column(name = "released_sent_date")
  private Instant releasedSentDate;

  @Column(name = "released_sent_date_tz")
  private String releasedSentDateTz;

  @Column(name = "cancelled_sent_date")
  private Instant cancelledSentDate;

  @Column(name = "cancelled_sent_date_tz")
  private String cancelledSentDateTz;

  @Transient
  public ZonedDateTime getReceivedSentDate() {
    if (receivedSentDate == null || receivedSentDateTz == null) {
      return null;
    }
    return receivedSentDate.atZone(ZoneId.of(receivedSentDateTz));
  }

  @Transient
  public void setReceivedSentDate(ZonedDateTime zdt) {
    this.receivedSentDate = zdt.toInstant();
    this.receivedSentDateTz = zdt.getOffset().getId();
  }

  @Transient
  public ZonedDateTime getReleasedSentDate() {
    if (releasedSentDate == null || releasedSentDateTz == null) {
      return null;
    }

    return releasedSentDate.atZone(ZoneId.of(releasedSentDateTz));
  }

  @Transient
  public void setReleasedSentDate(ZonedDateTime zdt) {
    this.releasedSentDate = zdt.toInstant();
    this.releasedSentDateTz = zdt.getOffset().getId();
  }


  @Transient
  public ZonedDateTime getCancelledSentDate() {
    if (cancelledSentDate == null || cancelledSentDateTz == null) {
      return null;
    }
    return cancelledSentDate.atZone(ZoneId.of(cancelledSentDateTz));
  }

  @Transient
  public void setCancelledSentDate(ZonedDateTime zdt) {
    this.cancelledSentDate = zdt.toInstant();
    this.cancelledSentDateTz = zdt.getOffset().getId();
  }

  @Transient
  public ZonedDateTime getDoneSentDate() {
    if (doneSentDate == null || doneSentDateTz == null) {
      return null;
    }
    return doneSentDate.atZone(ZoneId.of(doneSentDateTz));
  }

  @Transient
  public void setDoneSentDate(ZonedDateTime zdt) {
    this.doneSentDate = zdt.toInstant();
    this.doneSentDateTz = zdt.getOffset().getId();
  }

  @Transient
  public ZonedDateTime getReminderSentDate() {
    if (reminderSentDate == null || reminderSentDateTz == null) {
      return null;
    }
    return reminderSentDate.atZone(ZoneId.of(reminderSentDateTz));
  }

  @Transient
  public void setReminderSentDate(ZonedDateTime zdt) {
    this.reminderSentDate = zdt.toInstant();
    this.reminderSentDateTz = zdt.getOffset().getId();
  }

  public RegistrationEmail() {}

  public RegistrationEmail(Registration registration) {
    this.registration = registration;
  }

  public RegistrationEmail(Registration registration, ZonedDateTime receivedSentDate) {
    this.registration = registration;
    this.setReceivedSentDate(receivedSentDate);
  }
}
