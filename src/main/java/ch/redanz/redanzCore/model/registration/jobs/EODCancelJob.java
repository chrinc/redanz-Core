package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODCancelJob {

  @Autowired
  private Environment environment;

  private final WorkflowTransitionService workflowTransitionService;
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;
  private final WorkflowStatusService workflowStatusService;
  private final OutTextService outTextService;
  //  @Scheduled(cron = "${cron.matching.scheduler.value}")
//  @Scheduled(cron = "0 47 15 * * MON-SUN")
  @Scheduled(cron = "0 0/2 * * * *")
  public void runCancelJob() {
    registrationService.getAllConfirmingRegistrations().forEach(registration -> {
      RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
      LocalDateTime reminderSentDate = registrationEmail.getReminderSentDate();
      LocalDateTime deadline = LocalDateTime.now().minusDays(
        Long.parseLong(Objects.requireNonNull(environment.getProperty("registration.cancel.after.days")))
      );
      if (reminderSentDate != null && reminderSentDate.isBefore(deadline)) {
        try {
          workflowTransitionService.setWorkflowStatus(
            registration,
            workflowStatusService.getCancelled()
          );
          registrationEmailService.sendCancellationEmail(registration, registrationEmail);
          registrationService.updateSoldOut();
        } catch (IOException | TemplateException e) {
          throw new RuntimeException(e);
        }
      }
    });
  }

}
