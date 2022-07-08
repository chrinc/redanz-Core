package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
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
  private final WorkflowTransitionService workflowTransitionService;
  private Map<RegistrationMatching, RegistrationMatching> matchingPairs;

  //  @Scheduled(cron = "${cron.matching.scheduler.value}")
//  @Scheduled(cron = "0 47 15 * * MON-SUN")
  @Scheduled(cron = "0 0/15 * * * *")
  public void runMatching() {
    doMatching(registrationMatchingService.findRegistrationMatchingByRegistration2IsNull());
  }

  private void doMatching(List<RegistrationMatching> registrationMatchings) {
    matchingPairs = new HashMap<>();


    registrationMatchings.forEach(baseMatcher -> {
      boolean baseMatcherHasPartnerEmail = baseMatcher.getPartnerEmail() != null;
      Registration baseMatcherRegistration1 = baseMatcher.getRegistration1();

      registrationMatchings.forEach(lookupMatcher -> {
        boolean lookupMatcherHasPartnerEmail = lookupMatcher.getPartnerEmail() != null;

        // lookup partner by email
        if (
          // exclude self comparison
          !baseMatcher.equals(lookupMatcher) &&

            // both registrations applied with a partner
            (
              baseMatcherHasPartnerEmail && lookupMatcherHasPartnerEmail &&

                // check email match
                isEmailMatch(baseMatcher, lookupMatcher) ||

                // otherwise none of the registrations can have a PartnerEmail
                !baseMatcherHasPartnerEmail && !lookupMatcherHasPartnerEmail
            ) &&

            // check registration match
            registrationIsMatch(baseMatcherRegistration1, lookupMatcher.getRegistration1()) &&

            // check workflow
            workflowIsMatch(baseMatcherRegistration1, lookupMatcher.getRegistration1()) &&

            // prevent double entries (R1xR2, R2xR1)
            !matchingPairs.containsKey(lookupMatcher)
        ) {
          matchingPairs.put(baseMatcher, lookupMatcher);
        }
      });
    });

    onFoundMatch();
  }

  private void onFoundMatch() {
    matchingPairs.forEach((baseMatching, lookupMatching) -> {

      // update registration_matching
      baseMatching.setRegistration2(lookupMatching.getRegistration1());
      lookupMatching.setRegistration2(baseMatching.getRegistration1());
      registrationMatchingService.save(baseMatching);
      registrationMatchingService.save(lookupMatching);

    });
  }

  private boolean isEmailMatch(RegistrationMatching baseMatcher, RegistrationMatching lookupMatcher) {
    return (
      baseMatcher.getPartnerEmail().equals(lookupMatcher.getRegistration1().getParticipant().getUser().getEmail()) &&
        lookupMatcher.getPartnerEmail().equals(baseMatcher.getRegistration1().getParticipant().getUser().getEmail())
    );
  }

  private boolean workflowIsMatch(Registration baseRegistration, Registration lookupRegistration) {
    WorkflowTransition baseWorkflowTransition = workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(baseRegistration);
    WorkflowTransition lookupWorkflowTransition = workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(lookupRegistration);
    return (
      baseWorkflowTransition.getWorkflowStatus().getName().equals(WorkflowStatusConfig.SUBMITTED.getName()) &&
        lookupWorkflowTransition.getWorkflowStatus().getName().equals(WorkflowStatusConfig.SUBMITTED.getName())
    );
  }

  private boolean registrationIsMatch(Registration baseRegistration, Registration lookupRegistration) {
    return
      (
        // check bundles
        baseRegistration.getBundle().equals(lookupRegistration.getBundle()) &&
          baseRegistration.getTrack().equals(lookupRegistration.getTrack()) &&
          (
            // check dance roles
            !baseRegistration.getDanceRole().equals(lookupRegistration.getDanceRole())
              ||
              (
                baseRegistration.getDanceRole().equals(lookupRegistration.getDanceRole()) &&
                  baseRegistration.getDanceRole().getName().equals(DanceRoleConfig.SWITCH.getName())
              )
          )
      );
  }
}
