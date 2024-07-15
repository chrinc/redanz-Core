package ch.redanz.redanzCore.service.email;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Configuration
public class EmailConfig {

  @Value("${email.host.username}")
  private String hostEmail;

  @Value("${email.host.password}")
  private String hostPassword;

  @Value("${email.smtp.host}")
  private String emailSmtpHost;

  @Value("${email.debug}")
  private String emailDebug;

  @Value("${email.smtp.port}")
  private String emailSmtpPort;

  @Bean
  public Session emailSession() {
    Properties props = new Properties();
    props.put("mail.smtp.host", emailSmtpHost); // SMTP
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
    props.put("mail.smtp.socketFactory.port", 465);
    props.put("mail.smtp.port", emailSmtpPort); // SMTP Port
    props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
    props.put("mail.smtp.starttls.enable", "true"); // SMTP Port
    props.setProperty("mail.debug", emailDebug);
    props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    Authenticator auth = new Authenticator() {
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(hostEmail, hostPassword);
      }
    };
    return Session.getDefaultInstance(props, auth);
  }
}
