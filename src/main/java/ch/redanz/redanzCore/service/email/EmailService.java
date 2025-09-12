package ch.redanz.redanzCore.service.email;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {

  private final String hostEmail;
  private final String emailHostName;
  private final boolean sendEmail;
  private final boolean sendToTestEmail;
  private final String testEmail;
  private final Session emailSession;

  private final AsyncEmailService asyncEmailService;

  @Autowired
  public EmailService(
    @Value("${email.host.username}") String hostEmail,
    @Value("${email.host.name}") String emailHostName,
    @Value("${email.send}") boolean sendEmail,
    @Value("${email.sendToTestMail}") boolean sendToTestMailEmail,
    @Value("${email.testEmail}") String testEmail,
    Session emailSession,
    AsyncEmailService asyncEmailService
  ) {
    this.hostEmail = hostEmail;
    this.emailHostName = emailHostName;
    this.sendEmail = sendEmail;
    this.sendToTestEmail = sendToTestMailEmail;
    this.testEmail = testEmail;
    this.emailSession = emailSession;
    this.asyncEmailService = asyncEmailService;
  }

  @Async
  public void sendEmail(
    String toEmail,
    String subject,
    String body,
    Boolean baseParTestMailOnly,
    String baseParTestEmail,
    Boolean eventInactive,
    String bccEmail
  ) {
    try {
      MimeMessage msg = new MimeMessage(emailSession);
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");
      msg.setFrom(new InternetAddress(hostEmail, emailHostName));
      msg.setReplyTo(InternetAddress.parse(hostEmail, false));
      msg.setSubject(subject, "UTF-8");
      msg.setContent(body, "text/html; charset=UTF-8");
      msg.setSentDate(new Date());

      boolean emailIsConfig = Arrays.stream(UserConfig.values())
        .anyMatch(userConfig -> Objects.equals(userConfig.getUsername(), toEmail));

      boolean bccIsConfig = Arrays.stream(UserConfig.values())
        .anyMatch(userConfig -> Objects.equals(userConfig.getUsername(), bccEmail));

      msg.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(
          (sendToTestEmail || baseParTestMailOnly || eventInactive) ?
            (baseParTestEmail != null ? baseParTestEmail : testEmail) : toEmail,
          false
        )
      );

      // bcc to sender for archive reasons
      if (bccEmail != null) {
        if (bccIsConfig) {
          msg.addRecipients(Message.RecipientType.BCC, hostEmail);
        } else {
          msg.addRecipients(Message.RecipientType.BCC, bccEmail);
        }
      }

      String emailTo =
        (!emailIsConfig && sendEmail) ? (
          (sendToTestEmail || baseParTestMailOnly || eventInactive) ?
            (baseParTestEmail != null ? baseParTestEmail : testEmail) : toEmail
        ) : sendToTestEmail ?  testEmail : "nobody";
      log.info("send email, send to: " + emailTo);
      log.info("bcc: " + bccEmail);
      if (!emailIsConfig && sendEmail) {
        asyncEmailService.sendEmail(msg);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
