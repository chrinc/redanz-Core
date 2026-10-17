package ch.redanz.redanzCore.service.email;

import ch.redanz.redanzCore.model.workshop.entities.EmailAccount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class EmailService {

  // configuration: fallback if no email account is configured
  private final String hostEmail;
  private final String emailHostName;
  private final boolean sendEmail;
  private final boolean sendToTestEmail;
  private final String testEmail;
  private final Session emailSession;
  private final String hostPassword;
  private final EmailAccount configImapAccount; // null if email.imap.host is not set

  private final EmailAccountService emailAccountService;
  private final BaseParService baseParService;

  @Autowired
  public EmailService(
    @Value("${email.host.email}") String hostEmail,
    @Value("${email.host.name}") String emailHostName,
    @Value("${email.send}") boolean sendEmail,
    @Value("${email.sendToTestMail}") boolean sendToTestMailEmail,
    @Value("${email.testEmail:}") String testEmail, // optional
    @Value("${email.host.password}") String hostPassword,
    @Value("${email.imap.host:}") String imapHost, // optional: sent folder for the configuration fallback
    @Value("${email.imap.port:993}") Integer imapPort,
    @Value("${email.imap.security:SSL}") String imapSecurity,
    Session emailSession,
    EmailAccountService emailAccountService,
    BaseParService baseParService
  ) {
    this.hostEmail = hostEmail;
    this.emailHostName = emailHostName;
    this.sendEmail = sendEmail;
    this.sendToTestEmail = sendToTestMailEmail;
    this.testEmail = testEmail;
    this.emailSession = emailSession;
    this.hostPassword = hostPassword;
    this.configImapAccount = configImapAccount(hostEmail, imapHost, imapPort, imapSecurity);
    this.emailAccountService = emailAccountService;
    this.baseParService = baseParService;
  }

  // sender address of the type (also shown in the email footer): type, else main account, else configuration
  public String senderAddress(EmailAccount.Type accountType) {
    EmailAccount account = emailAccountService.resolveSender(accountType);
    return account == null ? hostEmail : account.getEmail();
  }

  /*
   * accountType: account the email is sent from (null = main); reply-to is the sender
   * event:       test mode also applies if the base parameter "test mail only" of the event is set (may be null)
   *
   * Test mode: sent from the testing account to the testing address (testers get their own emails),
   * subject " *** Test ***". The email is stored in the "sent" folder of the sending account.
   * If sending fails, the main account and then the configuration are used.
   */
  @Async
  public void sendEmail(
    String toEmail,
    String subject,
    String body,
    Boolean emailIsTester,
    Boolean eventInactive,
    byte[] icsAttachment,
    String icsFilename,
    EmailAccount.Type accountType,
    Event event
  ) {
    if (eventInactive) {
      log.warn("Event is inactive! / No Email should be sent: {}", subject);
      return;
    }

    boolean testMode = sendToTestEmail || (event != null && baseParService.testMailOnly(event));
    // testing address (static data), else email.testEmail (config), else host email
    String testingEmail = emailAccountService.testingEmail() != null ? emailAccountService.testingEmail()
      : (testEmail != null && !testEmail.isBlank()) ? testEmail
      : hostEmail;
    String recipient = testMode && !emailIsTester ? testingEmail : toEmail;
    String finalSubject = testMode ? subject + " *** Test ***" : subject;

    for (Sender sender : senders(testMode ? EmailAccount.Type.TESTING : accountType)) {
      try {
        MimeMessage msg = buildMessage(sender, recipient, finalSubject, body, icsAttachment, icsFilename);
        if (!sendEmail || msg.getRecipients(Message.RecipientType.TO).length == 0) {
          return;
        }
        Transport.send(msg);
        if (sender.account != null) {
          emailAccountService.storeInSent(sender.account, msg);
        } else if (configImapAccount != null) {
          emailAccountService.storeInSent(configImapAccount, hostPassword, msg);
        }
        return;
      } catch (Exception exception) {
        log.error("sending email from " + sender.email + " failed, trying next sender", exception);
      }
    }
    log.error("email could not be sent: {} -> {}", finalSubject, recipient);
  }

  // IMAP settings of the configuration (user = host email, password = host password)
  private static EmailAccount configImapAccount(String hostEmail, String imapHost, Integer imapPort, String imapSecurity) {
    if (imapHost == null || imapHost.isBlank()) {
      return null;
    }
    EmailAccount account = new EmailAccount();
    account.setEmail(hostEmail);
    account.setUsername(hostEmail);
    account.setImapHost(imapHost);
    account.setImapPort(imapPort);
    account.setImapSecurity(EmailAccount.Security.valueOf(imapSecurity));
    return account;
  }

  // sender of the type, then the main account, then the configuration
  private List<Sender> senders(EmailAccount.Type accountType) {
    List<Sender> senders = new ArrayList<>();
    EmailAccount primary = emailAccountService.resolveSender(accountType);
    if (primary != null) {
      senders.add(new Sender(primary));
    }
    EmailAccount main = emailAccountService.findMainSender();
    if (main != null && (primary == null || main.getAccountType() != primary.getAccountType())) {
      senders.add(new Sender(main));
    }
    senders.add(new Sender(null));
    return senders;
  }

  private MimeMessage buildMessage(
    Sender sender,
    String recipient,
    String subject,
    String body,
    byte[] icsAttachment,
    String icsFilename
  ) throws Exception {
    MimeMessage msg = new MimeMessage(sender.session);
    msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
    msg.addHeader("format", "flowed");
    msg.addHeader("Content-Transfer-Encoding", "8bit");
    msg.setFrom(new InternetAddress(sender.email, sender.name));
    msg.setReplyTo(InternetAddress.parse(sender.email, false));
    msg.setSubject(subject, "UTF-8");
    msg.setSentDate(new Date());
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient, false));

    MimeBodyPart htmlPart = new MimeBodyPart();
    htmlPart.setContent(body, "text/html; charset=UTF-8");

    MimeMultipart multipart = new MimeMultipart();
    multipart.addBodyPart(htmlPart);

    if (icsAttachment != null && icsAttachment.length > 0) {
      // plain file attachment: as "text/calendar" mail clients show it as an invitation (RSVP)
      MimeBodyPart calendarPart = new MimeBodyPart();
      calendarPart.setDataHandler(new DataHandler(new ByteArrayDataSource(icsAttachment, "application/ics")));
      calendarPart.setFileName(icsFilename.endsWith(".ics") ? icsFilename : icsFilename + ".ics");
      calendarPart.setDisposition(Part.ATTACHMENT);
      multipart.addBodyPart(calendarPart);
    }

    msg.setContent(multipart);
    msg.saveChanges();
    return msg;
  }

  // email account or configuration (account == null)
  private class Sender {
    private final EmailAccount account;
    private final Session session;
    private final String email;
    private final String name;

    private Sender(EmailAccount account) {
      this.account = account;
      this.session = account == null ? emailSession : emailAccountService.smtpSession(account);
      this.email = account == null ? hostEmail : account.getEmail();
      this.name = account == null || account.getDisplayName() == null || account.getDisplayName().isBlank()
        ? emailHostName : account.getDisplayName();
    }
  }
}
