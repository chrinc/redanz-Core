package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODReleaseJob {
  private final RegistrationService registrationService;
  private final RegistrationReleaseService registrationReleaseService;

  @Autowired
  Configuration mailConfig;

//  @Scheduled(cron = "0 50 15 * * MON-SUN")
//  @Scheduled(cron = "0 0/2 * * * *")

  @Scheduled(cron = "${cron.matching.scheduler.value.release}")
  public void runRelease() {

      log.info("Job: runRelease");
      registrationService.getAllSubmittedRegistrations().forEach(registration -> {
        registrationReleaseService.doRelease(registration);
      });
      registrationService.updateSoldOut();
    }
}
