package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationReleaseService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODReleaseJob {
  private final RegistrationService registrationService;
  private final RegistrationReleaseService registrationReleaseService;
  private final EventService eventService;
  private final BaseParService baseParService;

  @Autowired
  Configuration mailConfig;

//  @Scheduled(cron = "0 50 15 * * MON-SUN")
//  @Scheduled(cron = "0 0/2 * * * *")

  @Scheduled(cron = "${cron.matching.scheduler.value.release}")
  public void runRelease() {
    if (baseParService.doEODRelease()) {
      log.info("Job: runRelease");
      eventService.getActiveEventsFuture().forEach(event -> {
        registrationService.getAllSubmittedRegistrations(event).forEach(registration -> {
          registrationReleaseService.doRelease(registration);
        });
        registrationService.updateSoldOut(event);
      });
    }
  }
}
