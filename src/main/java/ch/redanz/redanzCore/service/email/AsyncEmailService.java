package ch.redanz.redanzCore.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;

@Service
@Slf4j
public class AsyncEmailService {

//  @Async
  public void sendEmail(MimeMessage msg) {
    try {
      Transport.send(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
