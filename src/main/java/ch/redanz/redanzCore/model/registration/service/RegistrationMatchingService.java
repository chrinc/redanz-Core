package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationMatchingService {

  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowStatusService workflowStatusService;

  public void save(RegistrationMatching registrationMatching) {
    registrationMatchingRepo.save(registrationMatching);
  }

  public Optional<RegistrationMatching> findByRegistration1(Registration registration1) {
    return registrationMatchingRepo.findByRegistration1(registration1);
  }

  public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
  }

//  public List<RegistrationMatching> findRegistration2ISNullSubmittedCurrent(){
//    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(
//      workflowStatusService.getSubmitted(),
//      eventService.getCurrentEvent()
//    );
//  }
  public List<RegistrationMatching> findRegistration2ISNullSubmitted(Event event){
    List<RegistrationMatching> myList = registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      event
    );
    return myList;
  }

  void cleanupMatchingRequest(Registration registration, boolean totalCleanup) {
    if (registrationMatchingRepo.findByRegistration1(registration).isPresent()) {

      if (totalCleanup) {
        registrationMatchingRepo.deleteAllByRegistration1(registration);
      } else {
        RegistrationMatching registration1Matching = registrationMatchingRepo.findByRegistration1(registration).get();
        registration1Matching.setRegistration2(null);
        registration1Matching.setPartnerEmail(null);
        save(registration1Matching);
      }
    }

    if (registrationMatchingRepo.findByRegistration2(registration).isPresent()) {
      RegistrationMatching registration2Matching = registrationMatchingRepo.findByRegistration2(registration).get();
      registration2Matching.setRegistration2(null);
//      registration2Matching.setPartnerEmail(null);
      save(registration2Matching);
    }
  }

  public void updateMatchingRequest(Registration registration, RegistrationRequest request) {
    if (request.getTrackId() != null && registration.getTrack().getPartnerRequired()) {
      RegistrationMatching registrationMatching
        = registrationMatchingRepo.findByRegistration1(registration).isPresent() ?
          registrationMatchingRepo.findByRegistration1(registration).get() :
          new RegistrationMatching(registration);

      if (request.getPartnerEmail() != null) {
        registrationMatching.setPartnerEmail(String.valueOf(request.getPartnerEmail()));
      } else {
        registrationMatching.setPartnerEmail(null);
      }

      save(registrationMatching);
    } else {
      cleanupMatchingRequest(registration, true);
    }
  }

  public void doMatch(Registration registration1, Registration registration2) {
    if (registrationMatchingRepo.findByRegistration2(registration1).isPresent()) {
      RegistrationMatching oldMatching1 = registrationMatchingRepo.findByRegistration2(registration1).get();
      oldMatching1.setRegistration2(null);
      save(oldMatching1);
    }
    if (registrationMatchingRepo.findByRegistration2(registration2).isPresent()) {
      RegistrationMatching oldMatching2 = registrationMatchingRepo.findByRegistration2(registration2).get();
      oldMatching2.setRegistration2(null);
      save(oldMatching2);
    }

    RegistrationMatching registrationMatching1 = findByRegistration1(registration1).get();
    registrationMatching1.setRegistration2(registration2);

    RegistrationMatching registrationMatching2 = findByRegistration1(registration2).get();
    registrationMatching2.setRegistration2(registration1);

    save(registrationMatching1);
    save(registrationMatching2);
  }

  public RegistrationMatching lookupMatch (RegistrationMatching baseMatcher, Event event) {
    List<RegistrationMatching> registrationMatchings = findRegistration2ISNullSubmitted(event);
    boolean baseMatcherHasPartnerEmail = baseMatcher.getPartnerEmail() != null;
//    registrationMatchings.forEach(lookupMatcher -> {
//      log.info("inc baseMatcher.equals(lookupMatcher) {}", baseMatcher.equals(lookupMatcher));
//      log.info("inc registrationMatch {}", registrationIsMatch(baseMatcher.getRegistration1(), lookupMatcher.getRegistration1()));
//      log.info("inc workflowMatch {}", workflowIsMatch(baseMatcher.getRegistration1(), lookupMatcher.getRegistration1()));
//      log.info("inc email {}", baseMatcherHasPartnerEmail && lookupMatcher.getPartnerEmail() != null);
//      log.info("inc emailMatch {}", isEmailMatch(baseMatcher, lookupMatcher));
//      log.info("inc emailMatch 2 {}", !baseMatcherHasPartnerEmail && lookupMatcher.getPartnerEmail() == null);
//    });
    return registrationMatchings.stream().filter(lookupMatcher ->

      // exclude self comparison
      !baseMatcher.equals(lookupMatcher)

        // check registration match
        && registrationIsMatch(baseMatcher.getRegistration1(), lookupMatcher.getRegistration1())

        // check workflow
        && workflowIsMatch(baseMatcher.getRegistration1(), lookupMatcher.getRegistration1())

        // both registrations applied with a partner
        && (
        baseMatcherHasPartnerEmail && lookupMatcher.getPartnerEmail() != null

          // check email match
          && isEmailMatch(baseMatcher, lookupMatcher)

          // otherwise none of the registrations can have a PartnerEmail
          || !baseMatcherHasPartnerEmail && lookupMatcher.getPartnerEmail() == null
      )
    ).findFirst().orElse(null);
  }

  public void doMatching(Registration registration) {
    if (findByRegistration1(registration).isPresent()){
      RegistrationMatching baseMatcher = findByRegistration1(registration).get();
      RegistrationMatching lookupMatcher = lookupMatch(baseMatcher, registration.getEvent());

      if (lookupMatcher != null) {
        updateRegistrationMatching(baseMatcher, lookupMatcher);
      }
    };
  }

  public void updateRegistrationMatching(RegistrationMatching baseMatcher, RegistrationMatching lookupMatcher){
    baseMatcher.setRegistration2(lookupMatcher.getRegistration1());
    lookupMatcher.setRegistration2(baseMatcher.getRegistration1());
    save(baseMatcher);
    save(lookupMatcher);
  }
  private boolean isEmailMatch(RegistrationMatching baseMatcher, RegistrationMatching lookupMatcher) {
    return (
      baseMatcher.getPartnerEmail().equalsIgnoreCase(lookupMatcher.getRegistration1().getParticipant().getUser().getUsername()) &&
        lookupMatcher.getPartnerEmail().equalsIgnoreCase(baseMatcher.getRegistration1().getParticipant().getUser().getUsername())
    );
  }

  private boolean workflowIsMatch(Registration baseRegistration, Registration lookupRegistration) {
    return (
      baseRegistration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())
        && lookupRegistration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())
    );
  }

  private boolean registrationIsMatch(Registration baseRegistration, Registration lookupRegistration) {
//    log.info("registrationIsMatch, baseRegistration Bundle: {}", baseRegistration.getBundle().getName());
//    log.info("registrationIsMatch, lookupRegistration Bundle: {}", lookupRegistration.getBundle().getName());
//    log.info("registrationIsMatch, baseRegistration BundleId: {}", baseRegistration.getBundle().getBundleId());
//    log.info("registrationIsMatch, lookupRegistration BundleId: {}", lookupRegistration.getBundle().getBundleId());
//    log.info("registrationIsMatch, bundleMatch: {}", baseRegistration.getBundle().getBundleId().equals(lookupRegistration.getBundle().getBundleId()));
//    log.info("registrationIsMatch, bundleMatch: {}", baseRegistration.getBundle().equals(lookupRegistration.getBundle()));
//    log.info("registrationIsMatch, not Sold Out: {}", !baseRegistration.getBundle().isSoldOut());
//    log.info("registrationIsMatch, track not sold out: {}", baseRegistration.getTrack().isSoldOut());
//    log.info("registrationIsMatch, danceRoles do not match: {}", !baseRegistration.getDanceRole().equals(lookupRegistration.getDanceRole()));
    return
      (
        // check bundles
        baseRegistration.getBundle().getBundleId().equals(lookupRegistration.getBundle().getBundleId())
          && !baseRegistration.getBundle().isSoldOut()

          // check tracks
          && (
               baseRegistration.getTrack().getTrackId().equals(lookupRegistration.getTrack().getTrackId())

           // with own partner, different Tracks are allowed
           || baseRegistration.getTrack().getOwnPartnerRequired()
          )
          && !baseRegistration.getTrack().isSoldOut() &&
          (
            // check dance roles
            !baseRegistration.getDanceRole().getDanceRoleId().equals(lookupRegistration.getDanceRole().getDanceRoleId())
              ||
              (
                baseRegistration.getDanceRole().equals(lookupRegistration.getDanceRole()) &&
                  baseRegistration.getDanceRole().getName().equals(DanceRoleConfig.SWITCH.getName())
              )

              // exception own partner required
              || baseRegistration.getTrack().getOwnPartnerRequired()
          )
      );
  }

  public void doMatching(Event event) {
    List<RegistrationMatching> registrationMatchings = findRegistration2ISNullSubmitted(event);
    Map<RegistrationMatching, RegistrationMatching> matchingPairs = new HashMap<>();

    registrationMatchings.forEach(
      baseMatcher -> {
        RegistrationMatching lookupMatcher = lookupMatch(baseMatcher, event);

        if (lookupMatcher != null
          && !matchingPairs.containsKey(lookupMatcher) && !matchingPairs.containsValue(baseMatcher)
          && !matchingPairs.containsKey(baseMatcher) && !matchingPairs.containsValue(lookupMatcher)
        ) {
            matchingPairs.put(baseMatcher, lookupMatcher);
          }

      }
    );

    onFoundMatch(matchingPairs);
  }

  private void onFoundMatch(Map<RegistrationMatching, RegistrationMatching> matchingPairs) {
    matchingPairs.forEach((baseMatching, lookupMatching) -> {
      updateRegistrationMatching(baseMatching, lookupMatching);
    });
  }


}
