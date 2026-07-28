package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationMatchingService {

  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowStatusService workflowStatusService;
  private final SpecialRegistrationService specialRegistrationService;
  private final RegistrationService registrationService;
  private final BundleService bundleService;
  private final BaseParService baseParRepo;
  private final BundleEventTrackService bundleEventTrackService;
  private final DanceRoleService danceRoleService;

  public void save(RegistrationMatching registrationMatching) {
    registrationMatchingRepo.save(registrationMatching);
  }

  public Optional<RegistrationMatching> findByRegistration1(Registration registration1) {
    return registrationMatchingRepo.findByRegistration1(registration1);
  }

  public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
  }

  public List<RegistrationMatching> findRegistration2ISNullSubmitted(Event event){
    List<RegistrationMatching> myList = registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      event
    );
    return myList;
  }

  public List<RegistrationMatching> findRegistration2ISNullSubmittedWithPartnerEmail(Event event){
    List<RegistrationMatching> myList = registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndPartnerEmailNotNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      event
    );
    return myList;
  }

  public List<RegistrationMatching> findRegistration2ISNullSubmittedWoutPartnerEmail(Event event){
    List<RegistrationMatching> myList = registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndPartnerEmailIsNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      event
    );
    return myList;
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

    registrationService.setIsRelease(registration1);
    registrationService.setIsRelease(registration2);
  }

  public RegistrationMatching lookupPartnerMatch (RegistrationMatching baseMatcher, Event event) {
    List<RegistrationMatching> registrationMatchings = findRegistration2ISNullSubmittedWithPartnerEmail(event);
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
          //   --|| !baseMatcherHasPartnerEmail && lookupMatcher.getPartnerEmail() == null
      )
    ).findFirst().orElse(null);
  }

  public void updatePartnersMatching(RegistrationMatching baseMatcher, RegistrationMatching lookupMatcher){
    doMatch(baseMatcher.getRegistration1(), lookupMatcher.getRegistration1());
    baseMatcher.setRegistration2(lookupMatcher.getRegistration1());
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
    return
      (
        // check bundles
        baseRegistration.getBundle().getBundleId().equals(lookupRegistration.getBundle().getBundleId())

          // check tracks
          // @todo: update new Flag getSameTrackRequired
          && (
             !baseRegistration.getTrack().getOwnPartnerRequired()
             && baseRegistration.getTrack().getTrackId().equals(lookupRegistration.getTrack().getTrackId())

           // with own partner only, different Tracks are required
           || (
                 baseRegistration.getTrack().getOwnPartnerRequired()
              && !baseRegistration.getTrack().getTrackId().equals(lookupRegistration.getTrack().getTrackId())
               )
          )
          &&
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
          && specialRegistrationService.registrationHasSpecialSpot(baseRegistration, lookupRegistration)
      );
  }

  public boolean hasPartnerMatch(RegistrationMatching baseMatcher, Event event) {
    Map<RegistrationMatching, RegistrationMatching> matchingPairs = new HashMap<>();
    RegistrationMatching lookupMatcher = lookupPartnerMatch(baseMatcher, event);

    return lookupMatcher != null
      &&  !matchingPairs.containsKey(lookupMatcher) && !matchingPairs.containsValue(baseMatcher)
      && !matchingPairs.containsKey(baseMatcher) && !matchingPairs.containsValue(lookupMatcher);
  }

  public void doPartnersMatching(RegistrationMatching registrationMatching, Event event) {
    RegistrationMatching partnerMatch = lookupPartnerMatch(registrationMatching, event);

    if (partnerMatch == null) {
      return;
    }

    Map<RegistrationMatching, RegistrationMatching> matchingPairs = new HashMap<>();
    matchingPairs.put(registrationMatching, partnerMatch);

    onFoundMatch(matchingPairs);
  }

  public boolean trackDanceRoleMatchingOk(Registration registration) {
    DanceRole role = registration.getDanceRole();
    if (role == null) {
      return true;
    }
    double switchWeight = baseParRepo.switchWeight(registration.getEvent()).doubleValue();
    double balanceScore = calculateTrackBalanceScore(registration, switchWeight);
    return isMatchingOkForRole(role, balanceScore);
  }

  public boolean trackDanceRoleMatchingRequired(Registration registration) {
    return registration.getBundle() != null
      && bundleService.hasTrack(registration.getBundle())
      && bundleEventTrackService
      .findByEventBundleAndTrack(registration.getEvent(), registration.getBundle(), registration.getTrack())
      .getBundleEventTrackDanceRoles()
      .size() > 1;
  }

  private double calculateTrackBalanceScore(Registration registration, double switchWeight) {
    int leadsAll = registrationService.countTracksConfirmingAndDone(
      registration.getTrack(),
      registration.getEvent(),
      danceRoleService.getLeadDanceRole()
    );

    int followAll = registrationService.countTracksConfirmingAndDone(
      registration.getTrack(),
      registration.getEvent(),
      danceRoleService.getFollowDanceRole()
    );

    int switchAll = registrationService.countTracksConfirmingAndDone(
      registration.getTrack(),
      registration.getEvent(),
      danceRoleService.getSwitchDanceRole()
    );

    return leadsAll - followAll - switchWeight * switchAll;
  }

  private double calculateSpecialBalanceScore(
    Registration registration,
    SpecialRegistration specialRegistration,
    double switchWeight
  ) {
    int leadsAll = specialRegistrationService.countEventSpecialsConfirmingDone(
      specialRegistration.getEventSpecial(),
      registration.getEvent(),
      danceRoleService.getLeadDanceRole()
    );

    int followAll = specialRegistrationService.countEventSpecialsConfirmingDone(
      specialRegistration.getEventSpecial(),
      registration.getEvent(),
      danceRoleService.getFollowDanceRole()
    );

    int switchAll = specialRegistrationService.countEventSpecialsConfirmingDone(
      specialRegistration.getEventSpecial(),
      registration.getEvent(),
      danceRoleService.getSwitchDanceRole()
    );

    return leadsAll - followAll - switchWeight * switchAll;
  }

  private boolean isMatchingOkForRole(DanceRole role, double balanceScore) {
    if (role.equals(danceRoleService.getLeadDanceRole())) {
      return balanceScore <= 2;
    }
    if (role.equals(danceRoleService.getFollowDanceRole())) {
      return balanceScore >= -2;
    }
    if (role.equals(danceRoleService.getSwitchDanceRole())) {
      return balanceScore >= -2;
    }
    return true;
  }

  public boolean specialDanceRoleMatchingOk(Registration registration) {
    DanceRole role = registration.getDanceRole();
    if (role == null) {
      return true;
    }

    double switchWeight = baseParRepo.switchWeight(registration.getEvent());
    return specialRegistrationService.findAllByRegistration(registration).stream()
      .allMatch(specialRegistration ->
        isMatchingOkForRole(
          role,
          calculateSpecialBalanceScore(registration, specialRegistration, switchWeight)
        )
      );
  }

  public boolean specialDanceRoleMatchingRequired(Registration registration) {
    return
      specialRegistrationService.findAllByRegistration(registration).size() > 0;

  }

  public boolean partnersMatchingRequired(Registration registration) {
    return findByRegistration1(registration).isPresent()
      && findByRegistration1(registration).get().getPartnerEmail() != null;

  }

  public boolean isRelease(Registration registration) {
    return

         // Sold Out
         !registrationService.isSoldOut(registration)

         // Reached Capacity
         && !registrationService.reachedCapacity(registration)

         // Partners Matching
         && (
              !partnersMatchingRequired(registration)
           || hasPartnerMatch(findByRegistration1(registration).get(), registration.getEvent())
         )
         // Track Role Matching
         && (
           !trackDanceRoleMatchingRequired(registration)
             || trackDanceRoleMatchingOk(registration)
         )
        // Special Role Matching
         && (
           !specialDanceRoleMatchingRequired(registration)
             || specialDanceRoleMatchingOk(registration)
         );


  }

  public void checkIsRelease(Registration registration) {
    if  (isRelease(registration)) {
      if (partnersMatchingRequired(registration)) {
        doPartnersMatching(findByRegistration1(registration).get(), registration.getEvent());
      } else {
        registrationService.setIsRelease(registration);
      }
    }
  };

  private void onFoundMatch(Map<RegistrationMatching, RegistrationMatching> matchingPairs) {
    matchingPairs.forEach((baseMatching, lookupMatching) -> {
      updatePartnersMatching(baseMatching, lookupMatching);
    });
  }

  public void removePartnerRegistration(Registration registration) {
    if (registrationMatchingRepo.existsByRegistration1(registration)) {
      RegistrationMatching registrationMatching1 = registrationMatchingRepo.findByRegistration1(registration).get();
      registrationMatching1.setRegistration2(null);
      save(registrationMatching1);
    }
    if (registrationMatchingRepo.existsByRegistration2(registration)) {
      RegistrationMatching registrationMatching2 = registrationMatchingRepo.findByRegistration2(registration).get();
      registrationMatching2.setRegistration2(null);
      save(registrationMatching2);
    }
  }
}
