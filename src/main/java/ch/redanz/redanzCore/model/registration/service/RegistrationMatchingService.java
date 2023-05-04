package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationMatchingService {

  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowStatusService workflowStatusService;
  private final EventService eventService;

  public void save(RegistrationMatching registrationMatching) {
    registrationMatchingRepo.save(registrationMatching);
  }

  public Optional<RegistrationMatching> findByRegistration1(Registration registration1) {
    return registrationMatchingRepo.findByRegistration1(registration1);
  }

  public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
  }

  public List<RegistrationMatching> findRegistration2ISNullSubmittedCurrent(){
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      eventService.getCurrentEvent()
    );
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

  public RegistrationMatching lookupMatch (RegistrationMatching baseMatcher) {
    List<RegistrationMatching> registrationMatchings = findRegistration2ISNullSubmittedCurrent();
    boolean baseMatcherHasPartnerEmail = baseMatcher.getPartnerEmail() != null;

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
      RegistrationMatching lookupMatcher = lookupMatch(baseMatcher);

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
      baseMatcher.getPartnerEmail().equals(lookupMatcher.getRegistration1().getParticipant().getUser().getEmail()) &&
        lookupMatcher.getPartnerEmail().equals(baseMatcher.getRegistration1().getParticipant().getUser().getEmail())
    );
  }

  private boolean workflowIsMatch(Registration baseRegistration, Registration lookupRegistration) {
    return (
      baseRegistration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())
        && lookupRegistration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())
    );
  }

  private boolean registrationIsMatch(Registration baseRegistration, Registration lookupRegistration) {
    return
      (
        // check bundles
        baseRegistration.getBundle().equals(lookupRegistration.getBundle()) &&
          !baseRegistration.getBundle().isSoldOut() &&
          baseRegistration.getTrack().equals(lookupRegistration.getTrack()) &&
          !baseRegistration.getTrack().isSoldOut() &&
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
