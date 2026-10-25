package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.reporting.response.ResponseReleaseCheck;
import ch.redanz.redanzCore.model.reporting.response.ResponseReleaseCheck.Check;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.service.BundleEventTrackService;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Objects;

// "release details": the conditions of the release job for one registration (same rules as RegistrationMatchingService.isRelease)
@Service
@AllArgsConstructor
public class ReleaseCheckService {
  private final RegistrationService registrationService;
  private final RegistrationMatchingService registrationMatchingService;
  private final SpecialRegistrationService specialRegistrationService;
  private final EventService eventService;
  private final BundleService bundleService;
  private final BundleEventTrackService bundleEventTrackService;
  private final BaseParService baseParService;
  private final WorkflowStatusService workflowStatusService;
  private final OutTextService outTextService;

  @Transactional(readOnly = true)
  public ResponseReleaseCheck check(Registration registration, Language language) {
    return check(registration, language, true);
  }

  // withPartner: also check whether the partner pulls the registration into the release
  private ResponseReleaseCheck check(Registration registration, Language language, boolean withPartner) {
    ResponseReleaseCheck result = new ResponseReleaseCheck();
    Event event = registration.getEvent();
    Bundle bundle = registration.getBundle();
    BundleEventTrack bundleEventTrack = bundle != null && bundleService.hasTrack(bundle)
      ? bundleEventTrackService.findByEventBundleAndTrack(event, bundle, registration.getTrack()) : null;

    result.setRegistrationId(registration.getRegistrationId());
    result.setEodRelease(baseParService.doEODRelease(event));
    result.setReleaseFlag(Boolean.TRUE.equals(registration.getIsRelease()));

    Integer waitListLength = baseParService.waitListLength(event);

    // event: sold out (effective, as in the overview), capacity (confirming + done)
    int eventCount = registrationService.countConfirmingAndDone(event);
    Check eventCheck = level("EVENT", event.getName(),
      eventService.getEventSoldOut(event), event.getSoldOutOverride() != null, eventCount, event.getCapacity());
    reason(eventCheck, "ALL_BUNDLES");
    result.getChecks().add(eventCheck);

    // track (counted over all passes with this track, capacity of the track in this pass)
    Check trackCheck = null;
    if (bundleEventTrack != null) {
      trackCheck = level("TRACK", registration.getTrack().getName(),
        bundleEventTrack.isSoldOut(), bundleEventTrack.getSoldOutOverride() != null,
        registrationService.countTracksConfirmingAndDone(registration.getTrack(), event), bundleEventTrack.getCapacity());
      waitList(trackCheck, registrationService.countTracksSubmittedConfirmingAndDone(registration.getTrack(), bundle, event), bundleEventTrack.getCapacity() + waitListLength);
      reason(trackCheck, capacityOrWaitList(registrationService.countTracksDone(registration.getTrack(), bundle, event), bundleEventTrack.getCapacity()));
    }

    if (bundle != null) {
      EventBundle eventBundle = eventService.findByEventAndBundle(event, bundle);
      Check bundleCheck = level("BUNDLE", bundle.getName(),
        eventBundle.isSoldOut(), eventBundle.getSoldOutOverride() != null,
        registrationService.countBundlesConfirmingAndDone(bundle, event), eventBundle.getCapacity());
      // passes without tracks: sold out if the wait list is full
      if (bundleEventTrack == null && eventBundle.getCapacity() != null) {
        waitList(bundleCheck, registrationService.countBundlesSubmittedConfirmingAndDone(bundle, event), eventBundle.getCapacity() + waitListLength);
      }
      // passes with tracks: sold out if all tracks are sold out
      reason(bundleCheck, bundleEventTrack != null ? "ALL_TRACKS"
        : capacityOrWaitList(registrationService.countBundlesDone(bundle, event), eventBundle.getCapacity()));

      if (trackCheck != null && bundleEventTrackService.findAllByEventAndBundle(event, bundle).size() == 1) {
        // pass with a single track: one line with the information of the track
        result.getChecks().add(mergeSingleTrack(bundleCheck, trackCheck));
        trackCheck = null;
      } else {
        result.getChecks().add(bundleCheck);
      }
    }

    if (trackCheck != null) {
      result.getChecks().add(trackCheck);
    }

    if (bundleEventTrack != null) {
      // dance role: closed ("yes"), balance in the track
      if (registration.getDanceRole() != null) {
        DanceRole danceRole = registration.getDanceRole();
        BundleEventTrackDanceRole trackRole = bundleEventTrack.getBundleEventTrackDanceRoles().stream()
          .filter(item -> item.getEventDanceRole().getDanceRole().equals(danceRole))
          .findFirst().orElse(null);
        boolean closed = trackRole != null && trackRole.getSoldOutStatus() == DanceRoleSoldOutStatus.SOLD_OUT;
        Check roleCheck = new Check();
        roleCheck.setLevel("ROLE");
        roleCheck.setItem(danceRole.getName());
        roleCheck.setSoldOut(closed);
        roleCheck.setManual(trackRole != null && trackRole.getSoldOutMode() != null);
        boolean balanceOk = true;
        if (registrationMatchingService.trackDanceRoleMatchingRequired(registration)) {
          double score = registrationMatchingService.calculateTrackBalanceScore(registration, baseParService.switchWeight(event));
          balanceOk = registrationMatchingService.isMatchingOkForRole(danceRole, score);
          roleCheck.setBalance(format(score));
          roleCheck.setBalanceLimit(balanceLimit(danceRole));
        }
        roleCheck.setOk(!closed && balanceOk);
        result.getChecks().add(roleCheck);
      }
    }

    // private classes and specials (balance of the dance role per special)
    specialRegistrationService.findAllPrivateClassesByRegistration(registration).stream()
      .filter(Objects::nonNull).map(PrivateClassRegistration::getEventPrivateClass)
      .forEach(privateClass -> {
        Check privateCheck = level("PRIVATE_CLASS", privateClass.getName(),
          Boolean.TRUE.equals(privateClass.getSoldOut()), privateClass.getSoldOutOverride() != null,
          specialRegistrationService.countPrivatesConfirmingAndDone(privateClass), privateClass.getCapacity());
        reason(privateCheck, "CAPACITY");
        result.getChecks().add(privateCheck);
      });
    specialRegistrationService.findAllByRegistration(registration).stream()
      .filter(Objects::nonNull)
      .forEach(specialRegistration -> {
        EventSpecial special = specialRegistration.getEventSpecial();
        Check specialCheck = level("SPECIAL", outText(special.getName(), language),
          Boolean.TRUE.equals(special.getSoldOut()), special.getSoldOutOverride() != null,
          specialRegistrationService.countEventSpecialsConfirmingAndDone(special, event), special.getCapacity());
        if (special.getCapacity() != null) {
          waitList(specialCheck, specialRegistrationService.countEventSpecialRegistrations(special, event), special.getCapacity() + waitListLength);
        }
        reason(specialCheck, capacityOrWaitList(specialRegistrationService.countEventSpecialsDone(special, event), special.getCapacity()));
        if (registration.getDanceRole() != null) {
          double score = registrationMatchingService.calculateSpecialBalanceScore(registration, specialRegistration, baseParService.switchWeight(event));
          specialCheck.setBalance(format(score));
          specialCheck.setBalanceLimit(balanceLimit(registration.getDanceRole()));
          specialCheck.setOk(specialCheck.isOk() && registrationMatchingService.isMatchingOkForRole(registration.getDanceRole(), score));
        }
        result.getChecks().add(specialCheck);
      });

    // partner
    Registration partner = null;
    if (registrationMatchingService.partnersMatchingRequired(registration)) {
      RegistrationMatching matching = registrationMatchingService.findByRegistration1(registration).get();
      if (matching.getRegistration2() != null) {
        partner = matching.getRegistration2();
      } else {
        RegistrationMatching partnerMatching = registrationMatchingService.lookupPartnerMatch(matching, event);
        partner = partnerMatching == null ? null : partnerMatching.getRegistration1();
      }
      Check partnerCheck = new Check();
      partnerCheck.setLevel("PARTNER");
      partnerCheck.setItem(matching.getPartnerEmail());
      partnerCheck.setPartner(matching.getPartnerEmail());
      partnerCheck.setPartnerRegistrationId(partner == null ? null : partner.getRegistrationId());
      partnerCheck.setOk(partner != null);
      result.getChecks().add(partnerCheck);
    }

    // once the job has set the release flag, only the event capacity is checked again
    boolean eventCapacityOk = eventCount < event.getCapacity();
    result.setWouldRelease(result.isReleaseFlag()
      ? eventCapacityOk
      : result.getChecks().stream().allMatch(Check::isOk));

    // pulled by the partner: if the partner passes his own checks, the job matches both (flag on both)
    // and releases this registration together with the partner, even if its own conditions fail
    if (withPartner && !result.isWouldRelease() && partner != null && eventCapacityOk
      && partner.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())) {
      ResponseReleaseCheck partnerResult = check(partner, language, false);
      boolean partnerOk = partnerResult.isReleaseFlag() || partnerResult.getChecks().stream()
        .filter(check -> !check.getLevel().equals("PARTNER"))
        .allMatch(Check::isOk);
      if (partnerOk) {
        result.getChecks().stream().filter(check -> !check.isOk()).forEach(check -> check.setPulled(true));
        result.setPulledByPartnerId(partner.getRegistrationId());
        result.setPulledByPartnerName(partner.getParticipant().getFirstName() + " " + partner.getParticipant().getLastName());
        result.setWouldRelease(true);
      }
    }
    return result;
  }

  private Check level(String level, String item, boolean soldOut, boolean manual, int count, Integer capacity) {
    Check check = new Check();
    check.setLevel(level);
    check.setItem(item);
    check.setSoldOut(soldOut);
    check.setManual(manual);
    check.setCount(count);
    check.setCapacity(capacity);
    check.setOk(!soldOut && (capacity == null || count < capacity));
    return check;
  }

  // line of the pass with the values of its only track; sold out by the track or manually on the pass
  private Check mergeSingleTrack(Check bundleCheck, Check trackCheck) {
    Check check = new Check();
    check.setLevel("BUNDLE");
    check.setItem(bundleCheck.getItem());
    check.setCount(trackCheck.getCount());
    check.setCapacity(trackCheck.getCapacity());
    check.setWaitListCount(trackCheck.getWaitListCount());
    check.setWaitListLimit(trackCheck.getWaitListLimit());
    Check soldOutBy = trackCheck.isSoldOut() ? trackCheck : bundleCheck;
    check.setSoldOut(soldOutBy.isSoldOut());
    check.setManual(soldOutBy.isManual());
    check.setSoldOutReason(trackCheck.isSoldOut() ? trackCheck.getSoldOutReason() : null);
    check.setOk(bundleCheck.isOk() && trackCheck.isOk());
    return check;
  }

  // reason of a computed sold out (manual values have no reason)
  private void reason(Check check, String reason) {
    if (check.isSoldOut() && !check.isManual()) {
      check.setSoldOutReason(reason);
    }
  }

  private static String capacityOrWaitList(int done, Integer capacity) {
    return capacity != null && done >= capacity ? "CAPACITY" : "WAIT_LIST";
  }

  // wait list: info, the sold out flag above already contains it
  private void waitList(Check check, int count, int limit) {
    check.setWaitListCount(count);
    check.setWaitListLimit(limit);
  }

  // name of a special is an out text key (as in the overview); key itself if there is no text
  private String outText(String key, Language language) {
    try {
      return outTextService.getOutTextByKeyAndLangKey(key, language.getLanguageKey()).getOutText();
    } catch (NoSuchElementException exception) {
      return key;
    }
  }

  private String balanceLimit(DanceRole danceRole) {
    return danceRole.getName().toLowerCase().contains("lead") ? "<= 2" : ">= -2";
  }

  private static String format(double value) {
    return String.format("%.2f", value);
  }
}
