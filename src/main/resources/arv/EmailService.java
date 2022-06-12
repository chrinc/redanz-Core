//package ch.redanz.redanzCore.service.email;
//
//import ch.redanz.redanzCore.model.profile.service.UserService;
//import ch.redanz.redanzCore.model.profile.service.PersonService;
//import freemarker.template.Configuration;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//
//@Service
//@Slf4j
//@AllArgsConstructor
//public class EmailService implements EmailSender {
//
////  private final JavaMailSender mailSender;
//  private final UserService userService;
//  private final PersonService personService;
//
//  @Autowired
//  Configuration mailConfig;
//
//  @Override
//  @Async
//  public void send(String to, String email, String subject) {}

//
//    System.out.println("TLSEmail Start");
//    Properties props = new Properties();
//    props.put("mail.smtp.host", "asmtp.mail.hostpoint.ch"); //SMTP Host
//    props.put("mail.smtp.port", "587"); //TLS Port
//    props.put("mail.smtp.auth", "true"); //enable authentication
//    props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

    //create Authenticator object to pass in Session.getInstance argument
//    Authenticator auth = new Authenticator() {
//      //override the getPasswordAuthentication method
//      protected PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication(email, password);
//      }
//    };
//    Session session = Session.getInstance(props, auth);
//    EmailUtil
//    EmailUtil.sendEmail(session, to,"TLSEmail Testing Subject", "TLSEmail Testing Body");




//    try{
//      MimeMessage mimeMessage = mailSender.createMimeMessage();
//      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
//      messageHelper.setText(email, true);
////      messageHelper.addAttachment("header.png", new ClassPathResource("emailHeader01.png"));
//      messageHelper.setTo(to);
//      messageHelper.setSubject(subject);
//      messageHelper.setFrom("registration@stirit.ch");
//      mailSender.send(mimeMessage);
//    } catch(MessagingException e) {
//      LOGGER.error("failded to send email", e);
//      throw new IllegalStateException("failed to send email");
//    }
//  }
//
//}
