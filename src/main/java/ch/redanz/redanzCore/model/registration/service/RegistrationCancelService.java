package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@Slf4j
public class RegistrationCancelService {
  private final BaseParService baseParService;
  private final RegistrationService registrationService;
  private final RegistrationEmailService registrationEmailService;
  // one transaction per registration: an error does not undo the cancellations before (their emails are sent)
  private final TransactionTemplate cancelTransaction;

  public RegistrationCancelService(
    BaseParService baseParService,
    RegistrationService registrationService,
    RegistrationEmailService registrationEmailService,
    PlatformTransactionManager transactionManager
  ) {
    this.baseParService = baseParService;
    this.registrationService = registrationService;
    this.registrationEmailService = registrationEmailService;
    this.cancelTransaction = new TransactionTemplate(transactionManager);
    this.cancelTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
  }

  public void doCancel(Event event){
    registrationService.getAllConfirmingRegistrations(event).forEach(registration -> {
      RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
      LocalDateTime reminderSentDate = registrationEmail != null && registrationEmail.getReminderSentDate() != null
        ? registrationEmail.getReminderSentDate().toLocalDateTime()
        : null;
      LocalDateTime deadline = LocalDateTime.now().minusDays(
        baseParService.cancelAfterDays(event)
      );
      if (reminderSentDate != null && reminderSentDate.isBefore(deadline)) {
        cancelTransaction.executeWithoutResult(status -> cancel(registrationService.findByRegistrationId(registration.getRegistrationId())));
      }
    });
  }

  private void cancel(Registration registration) {
    try {
      registrationService.onCancel(registration);
      registrationEmailService.sendCancellationEmail(registration, registrationEmailService.findByRegistration(registration));
    } catch (IOException | TemplateException e) {
      throw new RuntimeException(e);
    }
  }
}
