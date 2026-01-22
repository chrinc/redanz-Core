package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationCancelService {
  private final BaseParService baseParService;
  private final RegistrationService registrationService;
  private final RegistrationEmailService registrationEmailService;

  public void doCancel(Event event){
    registrationService.getAllConfirmingRegistrations(event).forEach(registration -> {
      RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
      LocalDateTime reminderSentDate = registrationEmail.getReminderSentDate().toLocalDateTime();
      LocalDateTime deadline = LocalDateTime.now().minusDays(
        baseParService.cancelAfterDays(event)
      );
      if (reminderSentDate != null && reminderSentDate.isBefore(deadline)) {
        try {
          registrationService.onCancel(registration);
          registrationEmailService.sendCancellationEmail(registration, registrationEmailService.findByRegistration(registration));
        } catch (IOException | TemplateException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
