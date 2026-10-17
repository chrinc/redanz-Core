package ch.redanz.redanzCore.model.workshop.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

// email account per type: emails are sent from this account and stored in its "sent" folder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email_account")
public class EmailAccount {

  public enum Type {
    MAIN,
    REGISTRATION,
    HOSTING,
    VOLUNTEERING,
    TESTING     // sender and recipient in test mode
  }

  public enum Security {
    STARTTLS,
    SSL,
    NONE
  }

  @Id
  @Enumerated(EnumType.STRING)
  @Column(name = "account_type", length = 20)
  private Type accountType;

  @Column(name = "email")
  private String email;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "smtp_host")
  private String smtpHost;

  @Column(name = "smtp_port")
  private Integer smtpPort;

  @Enumerated(EnumType.STRING)
  @Column(name = "smtp_security", length = 10)
  private Security smtpSecurity;

  @Column(name = "imap_host")
  private String imapHost;

  @Column(name = "imap_port")
  private Integer imapPort;

  @Enumerated(EnumType.STRING)
  @Column(name = "imap_security", length = 10)
  private Security imapSecurity;

  @Column(name = "username")
  private String username;

  // encrypted (jasypt, master password), never sent to the frontend
  @Column(name = "password_encrypted", length = 1000)
  private String passwordEncrypted;

  public boolean canSend() {
    return notBlank(email) && notBlank(smtpHost) && smtpPort != null && notBlank(username) && notBlank(passwordEncrypted);
  }

  public boolean canStoreSent() {
    return canSend() && notBlank(imapHost) && imapPort != null;
  }

  private static boolean notBlank(String value) {
    return value != null && !value.isBlank();
  }
}
