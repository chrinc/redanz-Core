package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import ch.redanz.redanzCore.service.log.ErrorLogType;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
  private final EventService eventService;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;
  private final RegistrationRepo registrationRepo;
  private final BundleService bundleService;
  private final TrackService trackService;
  private final DanceRoleService danceRoleService;
  private final PersonService personService;
  private final UserService userService;
  private final RegistrationMatchingService registrationMatchingService;
  private final FoodRegistrationService foodRegistrationService;
  private final HostingService hostingService;
  private final VolunteerService volunteerService;
  private final DonationRegistrationService donationRegistrationService;
  private final DiscountRegistrationService discountRegistrationService;
  private final RegistrationEmailService registrationEmailService;
  private final SpecialRegistrationService specialRegistrationService;
  private final SpecialService specialService;
  private final ErrorLogService errorLogService;
//  private final PrivateClassService privateClassService;
  private final LanguageService languageService;
  private final CountryService countryService;
  private final BaseParService baseParService;
  private final BundleEventTrackService bundleEventTrackService;

  public void update(Registration registration) {
    registrationRepo.save(registration);
  }

  public void updateSoldOut(Event event) {
    Integer waitListLength = baseParService.waitListLength();

    // Event
    if (countDone(event) >= eventService.getEventCapacity(event)) {
      if (!eventService.getEventSoldOut(event)) {
        event.setSoldOut(true);
        eventService.save(event);
      }
    } else {
      if (eventService.getEventSoldOut(event)) {
        event.setSoldOut(false);
        eventService.save(event);
      }
    }


    // Bundle
    AtomicBoolean allBundlesSoldOut = new AtomicBoolean(true);
    eventService.findAllEventBundles(event).forEach(eventBundle -> {
      Bundle bundle = eventBundle.getBundle();

      if (bundleService.hasTrack(bundle)) {
//        log.info("set Bundle: " + eventBundle.getBundle().getName() + " sold Out to false");
        if (eventBundle.isSoldOut()) {
          eventBundle.setSoldOut(false);
          eventService.save(eventBundle);
        }

        // Tracks
        bundle.getBundleEventTracks().forEach(bundleEventTrack -> {
          int trackCapacity = bundleEventTrack.getCapacity();
          Track track = bundleEventTrack.getEventTrack().getTrack();
          int countTracksDone = countTracksDone(track, bundle, event);

          boolean trackSoldOut =
            countTracksDone >= trackCapacity
              || countTracksSubmittedConfirmingAndDone(track, bundle, event) >= (trackCapacity + waitListLength);

//          log.info("track: " + track.getName() + " sold out?: " + trackSoldOut);
//          log.info("because countTracksDone: " + countTracksDone);
//          log.info(", trackCapacity: " + trackCapacity);
//          log.info(", countTracksSubmittedConfirmingAndDone: " + countTracksSubmittedConfirmingAndDone(track, bundle, event));
//          log.info(", trackCapacity: " +trackCapacity + " + waitListLength: " + waitListLength);

          if (trackSoldOut != bundleEventTrack.isSoldOut()) {
            bundleEventTrack.setSoldOut(trackSoldOut);
            bundleEventTrackService.save(bundleEventTrack);
          }

          // Dance Roles
          long noSwitchDanceRoleCount = bundleEventTrack.getBundleEventTrackDanceRoles().stream()
            .filter(bundleEventTrackDanceRole -> !bundleEventTrackDanceRole.getEventDanceRole().getDanceRole().equals(danceRoleService.getSwitchDanceRole()))
            .count();
          int danceRolesCount = noSwitchDanceRoleCount > 0 ? (int) noSwitchDanceRoleCount : 1;
          int open = trackCapacity - countTracksDone;

          bundleEventTrack.getBundleEventTrackDanceRoles().forEach(bundleEventTrackDanceRole -> {
            int roleOpen = countTracksSubmittedAndConfirmingByDanceRole(track, bundle, event, bundleEventTrackDanceRole.getEventDanceRole().getDanceRole());
            boolean danceRoleSoldOut = !bundleEventTrack.isSoldOut()
              && bundleEventTrackDanceRole.getEventDanceRole().getDanceRole() != danceRoleService.getSwitchDanceRole()
              && roleOpen >= (open / danceRolesCount) + waitListLength;
//            log.info("bundle: " + bundleEventTrackDanceRole.getBundleEventTrack().getBundle().getName());
//            log.info("role: " + bundleEventTrackDanceRole.getEventDanceRole().getDanceRole().getName());
//            log.info("roleOpenCount: " + roleOpen);
//            log.info("(open / danceRolesCount) + waitListLength: " + ((open / danceRolesCount) + waitListLength));

            if (danceRoleSoldOut != bundleEventTrackDanceRole.isSoldOut()) {
              bundleEventTrackDanceRole.setSoldOut(danceRoleSoldOut);
              bundleEventTrackService.save(bundleEventTrackDanceRole);
            }
          });
        });

      } else {
        var countBundlesDone = countBundlesDone(bundle, event);
        boolean bundlesSoldOut =
          // All done
          countBundlesDone >= eventBundle.getCapacity()
            // Wait list full
            || countBundlesSubmittedConfirmingAndDone(bundle, event) >= (eventBundle.getCapacity() + waitListLength);

        if (bundlesSoldOut != eventBundle.isSoldOut()) {
          eventBundle.setSoldOut(bundlesSoldOut);
          eventService.save(eventBundle);
        }
      }

      if (bundleService.hasTrack(bundle)) {
        AtomicBoolean allTracksSoldOut = new AtomicBoolean(true);
        bundle.getBundleEventTracks().forEach(bundleEventTrack -> {
          if (!bundleEventTrack.isSoldOut()) {
            allTracksSoldOut.set(false);
          }
        });

        if (allTracksSoldOut.get() != eventBundle.isSoldOut()
        ) {
          eventBundle.setSoldOut(allTracksSoldOut.get());
          eventService.save(eventBundle);
        }
      }

      if (!eventBundle.isSoldOut()) {
        allBundlesSoldOut.set(false);
      }
    });

    if (allBundlesSoldOut.get() != eventService.getEventSoldOut(event)) {
      event.setSoldOut(allBundlesSoldOut.get());
      eventService.save(event);
    }

    // Private Class
    eventService.findAllEventPrivates(event).forEach(
      eventPrivateClass -> {

        boolean privateClassSoldOut =
          eventPrivateClass.getCapacity()
            <= specialRegistrationService.countPrivateClassRegistrations(event, eventPrivateClass.getPrivateClass());

        if (privateClassSoldOut != eventPrivateClass.getSoldOut()) {
          eventPrivateClass.setSoldOut(privateClassSoldOut);
//          eventService.save(eventBundle);
        }
      });

    // Specials
    eventService.findAllEventSpecials(event).forEach(
      eventSpecial -> {
        boolean specialSoldOut =
          eventSpecial.getCapacity()
            <= specialRegistrationService.countSpecialRegistrations(eventSpecial.getSpecial(), event);

        if (specialSoldOut != eventSpecial.getSoldOut()) {
          eventSpecial.setSoldOut(specialSoldOut);
//          eventService.save(eventBundle);
        }
      });
  }

  public List<Registration> findAllByEvent(Event event) {
    return registrationRepo.findAllByEventAndActiveAndRegistrationType(event, true, RegistrationType.PARTICIPANT);
  }

  public List<WorkflowStatus> getWorkflowStatusList() {
    return workflowStatusService.findAll();
  }

  public int countActive(Event event) {
    return registrationRepo.countAllByEventAndActive(
      event, true
    );
  }
  public int countSubmittedConfirmingAndDone(Event event) {
    return countSubmitted(event) + countConfirming(event) + countDone(event);
  }
  public int countConfirmingAndDone(Event event) {
    return countConfirming(event) + countDone(event);
  }
  public int countSubmitted(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndRegistrationType(
      workflowStatusService.getSubmitted(), true, event, RegistrationType.PARTICIPANT
    );
  }
  public int countConfirming(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndRegistrationType(
      workflowStatusService.getConfirming(), true, event, RegistrationType.PARTICIPANT
    );
  }

  public int countByEvent(Event event) {
    return registrationRepo.countAllByEvent(event);
  }
  public int countDone(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndRegistrationType(
      workflowStatusService.getDone(),true, event, RegistrationType.PARTICIPANT
    );
  }
  public int countDone(Event event, DanceRole danceRole) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndDanceRole(
      workflowStatusService.getDone(),true, event, danceRole
    );
  }

  public int countSubmittedConfirmingAndDone(Event event, DanceRole danceRole) {
    return countSubmitted(event, danceRole) + countConfirming(event, danceRole) + countDone(event, danceRole);
  }

  public int countSubmitted(Event event, DanceRole danceRole) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndDanceRole(
      workflowStatusService.getSubmitted(), true, event, danceRole
    );
  }

  public int countConfirming(Event event, DanceRole danceRole) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEventAndDanceRole(
      workflowStatusService.getConfirming(), true, event, danceRole
    );
  }
  public int countBundlesSubmitted(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        bundle, workflowStatusService.getSubmitted(),true, event, RegistrationType.PARTICIPANT
      );
  }
  public int countBundlesSubmitted(Bundle bundle, Event event, DanceRole danceRole) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        bundle, workflowStatusService.getSubmitted(),true, event, danceRole
      );
  }
  public int countBundlesConfirming(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        bundle, workflowStatusService.getConfirming(), true, event, RegistrationType.PARTICIPANT
      );
  }
  public int countBundlesConfirming(Bundle bundle, Event event, DanceRole danceRole) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        bundle, workflowStatusService.getConfirming(), true, event, danceRole
      );
  }
  public int countBundlesDone(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        bundle, workflowStatusService.getDone(), true, event, RegistrationType.PARTICIPANT
      );
  }
  public int countBundlesDone(Bundle bundle, Event event, DanceRole danceRole) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        bundle, workflowStatusService.getDone(), true, event, danceRole
      );
  }
  public int countBundlesConfirmingAndDone(Bundle bundle, Event event) {
    return countBundlesConfirming(bundle, event) + countBundlesDone(bundle, event);
  }
  public int countBundlesSubmittedConfirmingAndDone(Bundle bundle, Event event) {
    return countBundlesConfirming(bundle, event)
      + countBundlesDone(bundle, event)
      + countBundlesSubmitted(bundle, event);
  }
  public int countBundlesSubmittedConfirmingAndDone(Bundle bundle, Event event, DanceRole danceRole) {
    return countBundlesConfirming(bundle, event, danceRole)
      + countBundlesDone(bundle, event, danceRole)
      + countBundlesSubmitted(bundle, event, danceRole);
  }

  public int countTracksConfirming(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, workflowStatusService.getConfirming(), true, event, RegistrationType.PARTICIPANT
      );
  }

  public int countTracksConfirming(Track track, Bundle bundle, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, bundle, workflowStatusService.getConfirming(), true, event, RegistrationType.PARTICIPANT
      );
  }

  public int countTracksConfirming(Track track, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, workflowStatusService.getConfirming(), true, event, danceRole
      );
  }

  public int countTracksConfirming(Track track, Bundle bundle, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, bundle, workflowStatusService.getConfirming(), true, event, danceRole
      );
  }
  public int countTracksSubmitted(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, workflowStatusService.getSubmitted(), true, event, RegistrationType.PARTICIPANT
      );
  }

  public int countTracksSubmitted(Track track, Bundle bundle, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, bundle, workflowStatusService.getSubmitted(), true, event, RegistrationType.PARTICIPANT
      );
  }

  public int countTracksSubmitted(Track track, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, workflowStatusService.getSubmitted(), true, event, danceRole
      );
  }
  public int countTracksSubmitted(Track track, Bundle bundle, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, bundle, workflowStatusService.getSubmitted(), true, event, danceRole
      );
  }
  public int countTracksDone(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, workflowStatusService.getDone(),true, event, RegistrationType.PARTICIPANT
      );
  }

  public int countTracksDone(Track track, Bundle bundle, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndRegistrationType(
        track, bundle, workflowStatusService.getDone(),true, event, RegistrationType.PARTICIPANT
      );
  }

//  public int countTracksSubmittedAndConfirmingByDanceRole(Track track, Event event, DanceRole danceRole) {
//    if (track == null) return 0;
//    return
//         registrationRepo.countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationType(
//           track, workflowStatusService.getSubmitted(), danceRole,true, event, RegistrationType.PARTICIPANT
//         )
//      +  registrationRepo.countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationType(
//        track, workflowStatusService.getConfirming(), danceRole,true, event, RegistrationType.PARTICIPANT
//      );
//  }
  public int countTracksSubmittedAndConfirmingByDanceRole(Track track, Bundle bundle, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
         registrationRepo.countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationTypeAndBundle(
           track, workflowStatusService.getSubmitted(), danceRole,true, event, RegistrationType.PARTICIPANT, bundle
         )
      +  registrationRepo.countAllByTrackAndWorkflowStatusAndDanceRoleAndActiveAndEventAndRegistrationTypeAndBundle(
        track, workflowStatusService.getConfirming(), danceRole,true, event, RegistrationType.PARTICIPANT, bundle
      );
  }
  public int countTracksDone(Track track, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, workflowStatusService.getDone(),true, event, danceRole
      );
  }
  public int countTracksDone(Track track, Bundle bundle, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndBundleAndWorkflowStatusAndActiveAndEventAndDanceRole(
        track, bundle, workflowStatusService.getDone(),true, event, danceRole
      );
  }
  public int countTracksConfirmingAndDone(Track track, Event event) {
    if (track == null) return 0;
    return countTracksDone(track, event) + countTracksConfirming(track, event);
  }

  public int countTracksSubmittedConfirmingAndDone(Track track, Event event) {
    if (track == null) return 0;
    return countTracksDone(track, event)
      + countTracksConfirming(track, event)
      + countTracksSubmitted(track, event);
  }

  public int countTracksSubmittedConfirmingAndDone(Track track, Bundle bundle, Event event) {
    if (track == null) return 0;
    return countTracksDone(track, bundle, event)
      + countTracksConfirming(track, bundle, event)
      + countTracksSubmitted(track, bundle, event);
  }
  public int countTracksSubmittedConfirmingAndDone(Track track, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return countTracksDone(track, event, danceRole)
      + countTracksConfirming(track, event, danceRole)
      + countTracksSubmitted(track, event, danceRole);
  }
  public int countTracksSubmittedConfirmingAndDone(Track track, Bundle bundle, Event event, DanceRole danceRole) {
    if (track == null) return 0;
    return countTracksDone(track, bundle, event, danceRole)
      + countTracksConfirming(track, bundle, event, danceRole)
      + countTracksSubmitted(track, bundle, event, danceRole);
  }

  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event, RegistrationType registrationType) {
    // log.info("inc@findByParticipantAndEvent");
    return registrationRepo.findByParticipantAndEventAndRegistrationTypeAndActive(participant, event, registrationType, true);
  }

  public void onCancel(Registration registration) throws TemplateException, IOException {
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getCancelled()
    );
    updateSoldOut(registration.getEvent());
  }

  public void onDelete(Registration registration) throws TemplateException, IOException {
    try {
      registration.setActive(false);

      update(registration);
      workflowTransitionService.setWorkflowStatus(
        registration,
        workflowStatusService.getDeleted()
      );
      updateSoldOut(registration.getEvent());
    } catch (Exception exception) {
      errorLogService.addLog(ErrorLogType.SUBMIT_DELETE.toString(), exception.toString());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SAVE_REGISTRATION_EN.getOutTextKey());
    }
  }

  public void onDeleteHost(Registration registration) throws TemplateException, IOException {
    hostingService.onDeleteHost(registration);
    if (isEmptyStaffRegistration(registration)) {
      registration.setActive(false);
      update(registration);
      workflowTransitionService.setWorkflowStatus(
        registration,
        workflowStatusService.getDeleted()
      );
    }
  }

  public void onDeleteHostee(Registration registration) throws TemplateException, IOException {
    hostingService.onDeleteHostee(registration);
    if (isEmptyStaffRegistration(registration)) {
      registration.setActive(false);
      update(registration);
      workflowTransitionService.setWorkflowStatus(
        registration,
        workflowStatusService.getDeleted()
      );
    }
  }
  public void onDeleteVolunteer(Registration registration) throws TemplateException, IOException {
    volunteerService.onDeleteVolunteer(registration);
    if (isEmptyStaffRegistration(registration)) {
      registration.setActive(false);
      update(registration);
      workflowTransitionService.setWorkflowStatus(
        registration,
        workflowStatusService.getDeleted()
      );
    }
  }

  public void onManualRelease(Registration registration) throws TemplateException, IOException {
    releaseToConfirming(registration);
    registrationEmailService.sendEmailConfirmation(registration, registrationEmailService.findByRegistration(registration));
  }

  public void releaseToConfirming(Registration registration) {
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getConfirming()
    );
    updateSoldOut(registration.getEvent());
  }

  public List<Registration> getAllConfirmingRegistrations(Event event) {
    return getAllByWorkflowStatus(
      workflowStatusService.getConfirming(), event
    );
  }

  public List<Registration> getAllSubmittedRegistrations(Event event) {
    return getAllByWorkflowStatus(
      workflowStatusService.getSubmitted(), event
    );
  }

  public List<Registration> getAllByWorkflowStatus(WorkflowStatus workflowStatus, Event event) {
    return registrationRepo.findAllByWorkflowStatusAndActiveAndEventAndRegistrationType(
      workflowStatus,
      true,
      event,
      RegistrationType.PARTICIPANT
    );
  }

  @Transactional
  public Registration updateRegistrationRequest(Long personId, Event event, JsonObject request) throws IOException, TemplateException {
    boolean isNewRegistration = false;
    // ignore if user already has a registration
    Registration registration;
    RegistrationRequest registrationRequest = new RegistrationRequest(
//      request.get("event").getAsJsonObject().get("eventId").getAsLong(),
      event.getEventId(),
      request.get("bundleId").getAsLong(),
      request.get("trackId").isJsonNull() ? null : request.get("trackId").getAsLong(),
      (request.get("danceRoleId") == null || request.get("danceRoleId").isJsonNull()) ? null : request.get("danceRoleId").getAsLong(),
      (request.get("partnerEmail") == null || request.get("partnerEmail").isJsonNull()) ? null : request.get("partnerEmail").getAsString()
    );

//     log.info("inc@updateRegistration, after registrationRequest");
    if (findByParticipantAndEvent(
      personService.findByPersonId(personId),
      event,
      RegistrationType.PARTICIPANT
    ).isPresent()) {
//         log.info("inc@updateRegistration, update Registration");
        // update Registration
        registration = findByParticipantAndEvent(
          personService.findByPersonId(personId),
          event,
          RegistrationType.PARTICIPANT
        ).get();
        updateRegistration(registration, registrationRequest, personId, RegistrationType.PARTICIPANT);
      }

    // new Registration
    else {
      try {
        isNewRegistration = true;
//         log.info("inc@updateRegistration, isNewRegistration");
        saveNewRegistration(registrationRequest, personId, RegistrationType.PARTICIPANT);
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Base Registration for personId: " + personId + ", request: " + request);
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SAVE_REGISTRATION_EN.getOutTextKey());
      }
//       log.info("inc@updateRegistration, bfr findByParticipantAndEvent");
      registration = findByParticipantAndEvent(
        personService.findByPersonId(personId),
        event,
        RegistrationType.PARTICIPANT
      ).get();

    }

    discountRegistrationService.saveCapacityDiscount(registration);
//     log.info("inc@updateRegistration, bfr food");
    foodRegistrationService.updateFoodRegistrationRequest(registration, request);
//     log.info("inc@updateRegistration, bfr discount");
    discountRegistrationService.updateDiscountRegistrationRequest(registration, request);
//     log.info("inc@updateRegistration, bfr special Reg");
    specialRegistrationService.updateSpecialRegistrationRequest(registration, request);
//     log.info("inc@updateRegistration, bfr privateClass Reg");
    specialRegistrationService.updatePrivateClassRequest(registration, request);
//     log.info("inc@updateRegistration, bfr Host");
    hostingService.updateHostRegistrationRequest(registration, request);
//     log.info("inc@updateRegistration, bfr Hostee");
    hostingService.updateHosteeRegistrationRequest(registration, request);
//     log.info("inc@updateRegistration, bfr Volunteer");
    volunteerService.updateVolunteerRequest(registration, request);
//     log.info("inc@updateRegistration, bfr Scholarship");
    donationRegistrationService.updateScholarshipRequest(registration, request);
//     log.info("inc@updateRegistration, bfr Donation");
    donationRegistrationService.updateDonationRequest(registration, request);
//    log.info("inc@updateRegistration, bfr matching");
    registrationMatchingService.updateMatchingRequest(registration, registrationRequest);
//    log.info("inc@updateRegistration, after matching");

//    log.info("inc@updateRegistration, isNewRegistration {}", isNewRegistration);
    if (isNewRegistration) {
      try {
        workflowTransitionService.setWorkflowStatus(
          registration
          ,workflowStatusService.getSubmitted()
        );
//        log.info("inc@updateRegistration, after setWorkflowStatus");
        registrationEmailService.sendRegistrationSubmittedEmail(registration);
//        log.info("inc@updateRegistration, after send Email");
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Update Workflow Status for personId: " + personId + ", request: " + request);
      }
    }

//    log.info("inc@updateRegistration, return");
    return registration;
  }

  @Transactional
  public void updateStaffRegistrationRequest(Event event, JsonObject request) throws IOException, TemplateException {
    boolean isNewRegistration = false;
    Registration registration;
    // ignore if user already has a registration

//    log.info("inc@updateStaffRegistrationRequest");
//    log.info(request.get("profile").getAsJsonObject().toString());
    JsonObject personRequest = request.get("profile").getAsJsonObject();

    Person person;

    if (
      personRequest.get("personId") != null
        && !personRequest.get("personId").isJsonNull()) {
      person = personService.findByPersonId(personRequest.get("personId").getAsLong());
      person.setFirstName(personRequest.get("firstName").getAsString());
      person.setLastName(personRequest.get("lastName").getAsString());
      person.setPostalCode(personRequest.get("postalCode").getAsString());
      person.setStreet(personRequest.get("street").getAsString());
      person.setCity(personRequest.get("city").getAsString());
      person.setEmail(personRequest.get("email").getAsString());
      if (personRequest.get("language") != null && !personRequest.get("language").isJsonNull()) {
        person.setPersonLang(languageService.findLanguageByLanguageKey(personRequest.get("language").getAsString()));
      }
      if (personRequest.get("mobile") != null && !personRequest.get("mobile").isJsonNull()) {
        person.setMobile(personRequest.get("mobile").getAsString());
      }
      person.setCountry(countryService.findCountry(personRequest.get("countryId").getAsLong()));
      person.setActive(true);
      personService.savePerson(person);

    } else {
      person = new Person(
        personRequest.get("firstName").getAsString()
        , personRequest.get("lastName").getAsString()
        , personRequest.get("street").getAsString()
        , personRequest.get("postalCode").getAsString()
        , personRequest.get("city").getAsString()
        , countryService.findCountry(personRequest.get("countryId").getAsLong())
        , personRequest.get("email").getAsString()
        , personRequest.get("mobile") == null ||personRequest.get("mobile").isJsonNull() ? null : personRequest.get("mobile").getAsString()
        , languageService.findLanguageByLanguageKey(personRequest.get("language").getAsString())
        , true
      );
    }

    personService.savePerson(person);

    if (findByParticipantAndEvent(
      person,
      event,
      RegistrationType.PARTICIPANT
    ).isPresent()) {
        // log.info("inc@updateRegistration, update Registration");
        // update Registration
        registration = findByParticipantAndEvent(
          person,
          event,
          RegistrationType.PARTICIPANT
        ).get();
      }
    else if (findByParticipantAndEvent(
      person,
      event,
      RegistrationType.STAFF
    ).isPresent()) {
      // log.info("inc@updateRegistration, update Registration");
      // update Registration
      registration = findByParticipantAndEvent(
        person,
        event,
        RegistrationType.STAFF
      ).get();
    }

    // new Registration
    else {
      try {
        isNewRegistration = true;
        // log.info("inc@updateRegistration, isNewRegistration");

        update(
          new Registration(
            person,
            event,
            RegistrationType.STAFF
          )
        );
        // log.info("inc@updateRegistration, bfr findByParticipantAndEvent");
        registration = findByParticipantAndEvent(
          person,
          event,
          RegistrationType.STAFF
        ).get();
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Base Registration for personId: " + person.getPersonId() + ", request: " + request);
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SAVE_REGISTRATION_EN.getOutTextKey());
      }
    }

    if (request.get("hosteeRegistration") != null
      && !request.get("hosteeRegistration").getAsJsonArray().isEmpty()) {
      hostingService.updateHosteeRegistrationRequest(registration, request);
    }

    if (request.get("hostRegistration") != null
      && !request.get("hostRegistration").getAsJsonArray().isEmpty()) {
      hostingService.updateHostRegistrationRequest(registration, request);
    }

    if (request.get("volunteerRegistration") != null && !request.get("volunteerRegistration").isJsonNull()) {
      volunteerService.updateVolunteerRequest(registration, request);
    }

    if (isNewRegistration) {
      try {
        workflowTransitionService.setWorkflowStatus(
          registration
          ,workflowStatusService.getDone()
        );
      } catch (Exception exception) {
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
      }
    }
  }

  public Registration getRegistration(Long userId, Event event, RegistrationType registrationType) {
    return registrationRepo.findByParticipantAndEventAndRegistrationTypeAndActive(
      personService.findByUser(userService.findByUserId(userId)),
      event,
      registrationType,
      true
    ).get();
  }
  public RegistrationResponse getRegistrationResponse(Registration registration) {
    // log.info("inc@getRegistrationResponse");
    RegistrationResponse registrationResponse = new RegistrationResponse(
      registration.getRegistrationId(),
      registration.getParticipant().getPersonId(),
      registration.getEvent().getEventId(),
      registration.getBundle() != null ? registration.getBundle().getBundleId() : null
    );
    registrationResponse.setEventId(registration.getEvent().getEventId());
    registrationResponse.setRegistrationId(registration.getRegistrationId());
    if (registration.getBundle() != null) {
      registrationResponse.setBundleId(registration.getBundle().getBundleId());
    }

    // track
    if (registration.getTrack() != null) {
      registrationResponse.setTrackId(registration.getTrack().getTrackId());
    }

    // dance role
    if (registration.getDanceRole() != null) {
      registrationResponse.setDanceRoleId(registration.getDanceRole().getDanceRoleId());
    }

    // partner Email
    RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
    if (registrationMatching != null) {
      registrationResponse.setPartnerEmail(registrationMatching.getPartnerEmail());
    }

    // workflow Status
//    log.info(registration.getWorkflowStatus().toString());
//    log.info(registration.getWorkflowStatus().getName());
    registrationResponse.setWorkflowStatus(registration.getWorkflowStatus());

    // Food Registration
    registrationResponse.setFoodRegistrations(
      foodRegistrationService.getAllByRegistration(registration)
    );

    // log.info("inc@getRegistrationResponse, bfr host");
    // Host Registration
    registrationResponse.setHostRegistration(
      hostingService.getHostRegistration(registration)
    );

    // Hostee Registration
    registrationResponse.setHosteeRegistration(
      hostingService.getHosteeRegistration(registration)
    );

    // Volunteer Registration
    registrationResponse.setVolunteerRegistration(
      volunteerService.getVolunteerRegistration(registration)
    );

    // Scholarship Registration
    registrationResponse.setScholarshipRegistration(
      donationRegistrationService.getScholarshipRegistration(registration)
    );

    // log.info("inc@getRegistrationResponse, bfr donation");
    // Donation Registration
    registrationResponse.setDonationRegistration(
      donationRegistrationService.getDonationRegistration(registration)
    );

    // Discounts
    registrationResponse.setDiscountRegistrations(
      discountRegistrationService.findAllByRegistration(registration)
    );

    // Specials
    registrationResponse.setSpecialRegistrations(
      specialRegistrationService.findAllByRegistration(registration)
    );

    // log.info("inc@getRegistrationResponse, bfr private Class");
    // Private Classes
    registrationResponse.setPrivateClassRegistrations(
      specialRegistrationService.findAllPrivateClassesByRegistration(registration)
    );

    // log.info("inc@getRegistrationResponse, bfr return, {}", registrationResponse);
    return registrationResponse;
  }

//  public RegistrationResponse getRegistrationResponse(Long personId, Long eventId, RegistrationType registrationType) {
  public RegistrationResponse getRegistrationResponse(Long personId, Long eventId) {
    Optional<Registration> registrationOptional =
      findByParticipantAndEvent(
        personService.findByPersonId(personId),
        eventService.findByEventId(eventId),
        RegistrationType.PARTICIPANT
      );
    if (!registrationOptional.isPresent()) {
      registrationOptional =
        findByParticipantAndEvent(
          personService.findByPersonId(personId),
          eventService.findByEventId(eventId),
          RegistrationType.STAFF
        );
    }

    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      RegistrationResponse registrationResponse = getRegistrationResponse(registration);
      // log.info("inc@bfr return response");
      return registrationResponse;
    } else {
//      return new RegistrationResponse(personId, eventService.findByEventId(eventId));
      return new RegistrationResponse(personId, eventId);
    }
  }

  public RegistrationResponse getNewRegistrationResponse(Long eventId) {
//      return new RegistrationResponse(eventService.findByEventId(eventId));
      return new RegistrationResponse(eventId);
  }

  public List<RegistrationResponse> getAllUserRegistrationResponses(Long personId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActive(
        personService.findByPersonId(personId), true
      );

    if (registrations != null) {
      registrations.forEach(registration -> {
        registrationResponses.add(getRegistrationResponse(registration));
      });
    }
    // log.info("bfr return responses");
    // log.info("bfr return registrationResponses {}", registrationResponses);
    return registrationResponses;
  }

  public List<RegistrationResponse> getUserActiveRegistrationResponses(Long personId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActiveAndEventArchivedAndEventActive(
        personService.findByPersonId(personId), true
    ,false
    ,true
      );

    if (registrations != null) {
      registrations.forEach(registration -> {
        registrationResponses.add(getRegistrationResponse(registration));
      });
    }
    return registrationResponses;
  }

  public List<RegistrationResponse> getUserInactiveRegistrationResponses(Long personId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
//    log.info("inc@getUserInactiveRegistrationResponses");
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActiveAndEventArchivedAndEventActive(
        personService.findByPersonId(personId)
        , true
        ,false
        ,false
      );

//    log.info("inc@getUserInactiveRegistrationResponses, registrations: {}", registrations);
    if (registrations != null) {
      registrations.forEach(registration -> {
        registrationResponses.add(getRegistrationResponse(registration));
      });
    }
    return registrationResponses;
  }

  private void saveNewRegistration(RegistrationRequest request, Long personId, RegistrationType registrationType) {
    Registration newRegistration = new Registration(
      eventService.findByEventId(request.getEventId()),
      bundleService.findByBundleId(request.getBundleId()),
      personService.findByPersonId(personId),
      registrationType
    );
    if (request.getTrackId() != null) {
      newRegistration.setTrack(trackService.findByTrackId(request.getTrackId()));
    }
    if (request.getDanceRoleId() != null) {
      newRegistration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    }
    update(newRegistration);
  }

  public Registration findByRegistrationId(Long registrationId){
    return registrationRepo.findByRegistrationId(registrationId);
  }

  private void updateRegistration(Registration registration, RegistrationRequest request, Long personId, RegistrationType registrationType) {
    registration.setEvent(eventService.findByEventId(request.getEventId()));
    registration.setBundle(bundleService.findByBundleId(request.getBundleId()));
    registration.setParticipant(personService.findByPersonId(personId));
    registration.setTrack(trackService.findByTrackId(request.getTrackId()));
    registration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    registration.setRegistrationType(registrationType);
    update(registration);
  }

  public List<Registration> findAllByEventStatusLangAndBundle(
    Event event, WorkflowStatus workflowStatus, Language language, Bundle bundle
  ) {
    return registrationRepo.findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLangAndBundle(
      true,event, workflowStatus, language, bundle
    );
  }
  public List<Registration> findAllByEventStatusAndLang(
    Event event, WorkflowStatus workflowStatus, Language language
  ) {
    return registrationRepo.findAllByActiveAndEventAndWorkflowStatusAndParticipantPersonLang(
      true,event, workflowStatus, language
    );
  }
  public List<Registration> findAllByEventStatusAndBundle(
    Event event, WorkflowStatus workflowStatus, Bundle bundle
  ) {
    return registrationRepo.findAllByActiveAndEventAndWorkflowStatusAndBundle(
      true,event, workflowStatus, bundle
    );
  }
  public List<Registration> findAllByEventAndStatus(
    Event event, WorkflowStatus workflowStatus
  ) {
    return registrationRepo.findAllByActiveAndEventAndWorkflowStatusAndRegistrationType(
      true,event, workflowStatus, RegistrationType.PARTICIPANT
    );
  }
  public List<Registration> findAllByEventLangAndBundle(
    Event event, Language language, Bundle bundle
  ) {
    return registrationRepo.findAllByActiveAndEventAndParticipantPersonLangAndBundle(
      true,event, language, bundle
    );
  }
  public List<Registration> findAllByEventAndLang(
    Event event, Language language
  ) {
    return registrationRepo.findAllByActiveAndEventAndParticipantPersonLang(
      true,event, language
    );
  }
  public List<Registration> findAllByEventAndBundle(
    Event event, Bundle bundle
  ) {
    return registrationRepo.findAllByActiveAndEventAndBundle(
      true,event, bundle
    );
  }

  private String formatCountToString(String formatedCount, int count, String pfx) {
    if (count > 0) {
      formatedCount = formatedCount.isBlank() ? "" : ", ";
      formatedCount = formatedCount + pfx + count;
      return formatedCount;
    }
    return "";
  }

  public List<String> countBundlesDoneAndSplitRoles(Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countBundlesDone(bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countBundlesDone(bundle, event, danceRole)
          ,danceRole.getName() + ": "
        ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countBundlesSubmittedConfirmingAndDoneAndSplitRoles(Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countBundlesSubmittedConfirmingAndDone(bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countBundlesSubmittedConfirmingAndDone(bundle, event, danceRole)
          ,danceRole.getName() + ": "
        ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countBundlesSubmittedAndSplitRoles(Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countBundlesSubmitted(bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countBundlesSubmitted(bundle, event, danceRole)
          ,danceRole.getName() + ": "
        ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countBundlesConfirmingAndSplitRoles(Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countBundlesConfirming(bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countBundlesConfirming(bundle, event, danceRole)
          ,danceRole.getName() + ": "
        ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksSubmittedConfirmingAndDoneAndSplitRoles(Track track, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksSubmittedConfirmingAndDone(track, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksSubmittedConfirmingAndDone(track, event, danceRole)
          ,danceRole.getName() + ": "
          ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksByBundleSubmittedConfirmingAndDoneAndSplitRoles(Track track, Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksSubmittedConfirmingAndDone(track, bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksSubmittedConfirmingAndDone(track, bundle, event, danceRole)
          ,danceRole.getName() + ": "
          ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksSubmittedAndSplitRoles(Track track, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksSubmitted(track, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksSubmitted(track, event, danceRole)
        ,danceRole.getName() + ": "
          ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksByBundleSubmittedAndSplitRoles(Track track, Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksSubmitted(track, bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksSubmitted(track, bundle, event, danceRole)
        ,danceRole.getName() + ": "
          ));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksConfirmingAndSplitRoles(Track track, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksConfirming(track, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksConfirming(track, event, danceRole)
        ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksByBundleConfirmingAndSplitRoles(Track track, Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksConfirming(track, bundle, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countTracksConfirming(track, bundle, event, danceRole)
        ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
  public List<String> countTracksDoneAndSplitRoles(Track track, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countTracksDone(track, event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          , countTracksDone(track, event, danceRole)
          , danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countTracksByBundleDoneAndSplitRoles(Track track, Bundle bundle, Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();

    count.add(String.valueOf(countTracksDone(track, bundle, event)));

    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          , countTracksDone(track, bundle, event, danceRole)
          , danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countSubmittedConfirmingAndDoneAndSplitRoles(Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSubmittedConfirmingAndDone(event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSubmittedConfirmingAndDone(event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }

  public List<String> countConfirmingAndSplitRoles(Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countConfirming(event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countConfirming(event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
  public List<String> countSubmittedAndSplitRoles(Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countSubmitted(event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countSubmitted(event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }
  public List<String> countDoneAndSplitRoles(Event event) {
    StringBuilder countPartBuilder = new StringBuilder();
    List<String> count = new ArrayList<>();
    count.add(String.valueOf(countDone(event)));
    danceRoleService.all().forEach(danceRole -> {
      countPartBuilder.append(
        formatCountToString(
          countPartBuilder.toString()
          ,countDone(event, danceRole)
          ,danceRole.getName() + ": "));
    });
    count.add(countPartBuilder.toString());
    return count;
  }


  public List<Registration> findAllByEventAndSlot(Event event, Slot slot) {
    return findAllByEvent(event)
      .stream()
      .filter(registration -> registration.getBundle().getPartySlots().contains(slot))
      .collect(Collectors.toList());
  }

  public boolean isEmptyStaffRegistration(Registration registration){
    return
         registration.getRegistrationType() == RegistrationType.STAFF
      && !hostingService.hasHosteeRegistration(registration)
      && !hostingService.hasHostRegistration(registration)
      && !volunteerService.hasVolunteerRegistration(registration);
  }
}
