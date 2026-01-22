package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationReminderService {
  private final BaseParService baseParService;
  private final RegistrationService registrationService;
  private final RegistrationEmailService registrationEmailService;

  public void doRemind(Event event){
    registrationService.getAllConfirmingRegistrations(event).forEach(registration -> {
      LocalDateTime releasedAt = registration.getWorkflowStatusDate().toLocalDateTime();
      LocalDateTime deadline = LocalDateTime.now().minusDays(
        baseParService.reminderAfterDays(event)
      );
      RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
      if (releasedAt.isBefore(deadline) && registrationEmail.getReminderSentDate() == null) {
        try {
          registrationEmailService.sendReminderEmail(registration, registrationEmail);
        } catch (IOException | TemplateException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }
}
