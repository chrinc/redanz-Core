package ch.redanz.redanzCore.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
@Slf4j
public class EmailService {
  private static String hostEmail;
  private static String hostPassword;
  private static String emailReceiverDev;
  private static String emailSmtpHost;
  private static String emailSmtpPort;
  private static String emailDebug;
  private static String emailHostName;
  private static boolean sendEmail;

  @Autowired
  public EmailService(
    @Value("${email.host.username}") String hostEmail,
    @Value("${email.host.password}") String hostPassword,
    @Value("${email.dev.receiver}") String emailReceiverDev,
    @Value("${email.smtp.host}") String emailSmtpHost,
    @Value("${email.debug}") String emailDebug,
    @Value("${email.host.name}") String emailHostName,
    @Value("${email.smtp.port}") String emailSmtpPort,
    @Value("${email.send}") boolean sendEmail
  ) {
    this.hostEmail = hostEmail;
    this.hostPassword = hostPassword;
    this.emailReceiverDev = emailReceiverDev;
    this.emailSmtpHost = emailSmtpHost;
    this.emailHostName = emailHostName;
    this.emailDebug = emailDebug;
    this.emailSmtpPort = emailSmtpPort;
    this.sendEmail = sendEmail;
  }

  @Bean("devSession")
  public static Session getSession() {
    Properties props = new Properties();
    props.put("mail.smtp.host", emailSmtpHost); // SMTP
    props.put("mail.smtp.socketFactory.class",
      "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
    props.put("mail.smtp.port", emailSmtpPort); // SMTP Port
    props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
    props.put("mail.smtp.starttls.enable", "true"); //SMTP Port
    props.setProperty("mail.debug", emailDebug);
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
    Authenticator auth = new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(hostEmail, hostPassword);
      }
    };
    return Session.getDefaultInstance(props, auth);
  }

  public static void sendEmail(Session session, String toEmail, String subject, String body) {
    try {
      MimeMessage msg = new MimeMessage(session);
      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");
      msg.setFrom(new InternetAddress(hostEmail, emailHostName));
      msg.setReplyTo(InternetAddress.parse(hostEmail, false));
      msg.setSubject(subject, "UTF-8");
      msg.setContent(body, "text/html; charset=UTF-8");
      msg.setSentDate(new Date());
      String emailReceiver = emailReceiverDev.isEmpty() ? toEmail : emailReceiverDev;
      msg.setRecipients(
        Message.RecipientType.TO, InternetAddress.parse(emailReceiver, false)
      );
      if (sendEmail) Transport.send(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
