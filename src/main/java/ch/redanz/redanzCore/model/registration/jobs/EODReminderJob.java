package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
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

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODReminderJob {

  @Autowired
  private Environment environment;
  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final BaseParService baseParService;

  @Scheduled(cron = "${cron.matching.scheduler.value.reminder}")
  public void runReminderJob() {

    if (baseParService.doEODReminder()) {
      log.info("Job: runReminder");
      eventService.getActiveEventsFuture().forEach(event -> {
        registrationService.getAllConfirmingRegistrations(event).forEach(registration -> {
          LocalDateTime releasedAt = registration.getWorkflowStatusDate();
          LocalDateTime deadline = LocalDateTime.now().minusDays(
            baseParService.reminderAfterDays()
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
      });
    }
  }
}
