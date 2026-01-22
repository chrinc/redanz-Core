package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODMatchingJob {

  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final BaseParService baseParService;

  //  @Scheduled(cron = "0 47 15 * * MON-SUN")
  //  @Scheduled(cron = "0 0/2 * * * *")
  @Scheduled(cron = "${cron.matching.scheduler.value.matching}")
  public void runMatching() {
    eventService.getActiveEventsFuture().forEach(event -> {
      if (baseParService.doEODMatching(event)) {
        log.info("Job: runMatching");
        registrationService.updateSoldOut(event);
        registrationMatchingService.doMatching(event);
      }
    });
  }

}
