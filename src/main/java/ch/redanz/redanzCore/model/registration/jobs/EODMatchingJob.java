package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODMatchingJob {

  private final RegistrationMatchingService registrationMatchingService;
  private Map<RegistrationMatching, RegistrationMatching> matchingPairs;
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final BaseParService baseParService;

  //  @Scheduled(cron = "0 47 15 * * MON-SUN")
  //  @Scheduled(cron = "0 0/2 * * * *")
  @Scheduled(cron = "${cron.matching.scheduler.value.matching}")
  public void runMatching() {
    if (baseParService.doEODMatching()) {
      log.info("Job: runMatching");
      registrationService.updateSoldOut(eventService.getCurrentEvent());
      doMatching(registrationMatchingService.findRegistration2ISNullSubmittedCurrent());
    }
  }

  private void doMatching(List<RegistrationMatching> registrationMatchings) {
    matchingPairs = new HashMap<>();

    registrationMatchings.forEach(
      baseMatcher -> {
        RegistrationMatching lookupMatcher = registrationMatchingService.lookupMatch(baseMatcher);

        if (lookupMatcher != null
          && !matchingPairs.containsKey(lookupMatcher) && !matchingPairs.containsValue(baseMatcher)
          && !matchingPairs.containsKey(baseMatcher) && !matchingPairs.containsValue(lookupMatcher)
        ) {
            matchingPairs.put(baseMatcher, lookupMatcher);
          }
      }
    );

    onFoundMatch();
  }
  private void onFoundMatch() {
    matchingPairs.forEach((baseMatching, lookupMatching) -> {
      registrationMatchingService.updateRegistrationMatching(baseMatching, lookupMatching);
    });
  }

}
