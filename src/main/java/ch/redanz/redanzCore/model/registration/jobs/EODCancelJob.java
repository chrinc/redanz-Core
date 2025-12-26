package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODCancelJob {

  @Autowired
  private Environment environment;

  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final BaseParService baseParService;


  @Scheduled(cron = "${cron.matching.scheduler.value.cancel}")
  @Transactional
  public void runCancelJob() {
    if (baseParService.doEODCancel()) {
      log.info("Job: runCancel");
      eventService.getActiveEventsFuture().forEach(event -> {
        registrationService.getAllConfirmingRegistrations(event).forEach(registration -> {
          RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
          LocalDateTime reminderSentDate = registrationEmail.getReminderSentDate().toLocalDateTime();
          LocalDateTime deadline = LocalDateTime.now().minusDays(
            baseParService.cancelAfterDays()
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
      });
    }
  }

}
