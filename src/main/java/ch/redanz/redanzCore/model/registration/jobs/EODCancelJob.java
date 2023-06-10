package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.*;
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
import java.util.Objects;

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
  public void runCancelJob() {
    if (baseParService.doEODCancel()) {
      log.info("Job: runCancel");
      Event currentEvent = eventService.getCurrentEvent();

      registrationService.getAllConfirmingRegistrations(currentEvent).forEach(registration -> {
        RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
        LocalDateTime reminderSentDate = registrationEmail.getReminderSentDate();
        LocalDateTime deadline = LocalDateTime.now().minusDays(
          Long.parseLong(Objects.requireNonNull(environment.getProperty("registration.cancel.after.days")))
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

}
