package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.entities.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Track;
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
  private final PrivateClassService privateClassService;

  public void update(Registration registration) {
    registrationRepo.save(registration);
  }

  public void updateSoldOut(Event event){

    // Event
    if (countDone(event) >= event.getCapacity()) {
      if (!event.isSoldOut()) {
        event.setSoldOut(true);
        eventService.save(event);
      }
    } else {
      if (event.isSoldOut()) {
        event.setSoldOut(false);
        eventService.save(event);
      }
    }

    // Bundle
    event.getEventBundles().forEach(eventBundle -> {
      Bundle bundle = eventBundle.getBundle();
      if (countBundlesDone(bundle, event) >= bundle.getCapacity()) {
        if (!bundle.isSoldOut()) {
          bundle.setSoldOut(true);
          bundleService.save(bundle);
        }
      } else {
        if (bundle.isSoldOut()) {
          bundle.setSoldOut(false);
          bundleService.save(bundle);
        }
      }

      specialService.findByEventOrBundle(event, bundle).forEach(special -> {
        specialRegistrationService.soldOut(
          special
         ,specialRegistrationService.countSpecialRegistrations(special) >= special.getCapacity()
        );
      });

      // Tracks
      if (!bundle.getBundleTracks().isEmpty()) {
        bundle.getBundleTracks().forEach(bundleTrack -> {
          Track track = bundleTrack.getTrack();
          if (countTracksDone(track, event) >= track.getCapacity()) {
            if (!track.isSoldOut()) {
              track.setSoldOut(true);
              trackService.save(track);
            }
          } else {
            if (track.isSoldOut()) {
              track.setSoldOut(false);
              trackService.save(track);
            }
          }
        });
      }
    });

    // Private Class
    privateClassService.findByEvent(event).forEach(privateClass -> {
        specialRegistrationService.soldOut(
          privateClass
          ,specialRegistrationService.countPrivateClassRegistrations(privateClass) >= privateClass.getCapacity()
        );
    });
  }

  public List<Registration> findAllCurrentEvent() {
    return registrationRepo.findAllByEventAndActive(eventService.getCurrentEvent(), true);
  }

  public List<Registration> findAllByEvent(Event event) {
    return registrationRepo.findAllByEventAndActive(event, true);
  }

  public List<WorkflowStatus> getWorkflowStatusList() {
    return workflowStatusService.findAll();
  }

  public int countAll() {
    return registrationRepo.countAllByEventAndActive(
      eventService.getCurrentEvent(), true
    );
  }
  public int countSubmittedConfirmingAndDone(Event event) {
    return countSubmitted(event) + countConfirming(event) + countDone(event);
  }
  public int countConfirmingAndDone(Event event) {
    return countConfirming(event) + countDone(event);
  }
  public int countSubmitted(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEvent(
      workflowStatusService.getSubmitted(), true, event
    );
  }
  public int countConfirming(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEvent(
      workflowStatusService.getConfirming(), true, event
    );
  }
  public int countDone(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndActiveAndEvent(
      workflowStatusService.getDone(),true, event
    );
  }
  private int countBundlesSubmitted(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEvent(
        bundle, workflowStatusService.getSubmitted(),true, event
      );
  }
  private int countBundlesConfirming(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEvent(
        bundle, workflowStatusService.getConfirming(), true, event
      );
  }
  public int countBundlesDone(Bundle bundle, Event event) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndActiveAndEvent(
        bundle, workflowStatusService.getDone(), true, event
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
  private int countTracksConfirming(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEvent(
        track, workflowStatusService.getConfirming(), true, event
      );
  }
  private int countTracksSubmitted(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEvent(
        track, workflowStatusService.getSubmitted(), true, event
      );
  }
  public int countTracksDone(Track track, Event event) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndActiveAndEvent(
        track, workflowStatusService.getDone(),true, event
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

  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event) {
    // log.info("inc@findByParticipantAndEvent");
    return registrationRepo.findByParticipantAndEventAndActive(participant, event, true);
  }
//  public Optional<Registration> findByParticipantAndEventRegular(Person participant, Event event) {
//    return registrationRepo.findByParticipantAndEventAndWorkflowStatusContaining(participant, event, workflowStatusService.findAllRegular());
//  }

  public void onCancel(Registration registration) throws TemplateException, IOException {
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getCancelled()
    );
    updateSoldOut(registration.getEvent());
  }

  public void onDelete(Registration registration) throws TemplateException, IOException {
    registration.setActive(false);
    update(registration);
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getDeleted()
    );
    updateSoldOut(registration.getEvent());
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

  public List<Registration> getAllConfirmingRegistrations() {
    return getAllByWorkflowStatus(
      workflowStatusService.getConfirming()
    );
  }

  public List<Registration> getAllSubmittedRegistrations() {
    return getAllByWorkflowStatus(
      workflowStatusService.getSubmitted()
    );
  }

  public List<Registration> getAllByWorkflowStatus(WorkflowStatus workflowStatus) {
    return registrationRepo.findAllByWorkflowStatusAndActiveAndEvent(
      workflowStatus,
      true,
      eventService.getCurrentEvent()
    );
  }

  @Transactional
  public Registration updateRegistrationRequest(Long userId, JsonObject request) throws IOException, TemplateException {
    // log.info("inc@updateRegistration, userId: {}", userId);
    // log.info("inc@updateRegistration, request: {}", request);
    boolean isNewRegistration = false;
    // ignore if user already has a registration
    Registration registration;
    RegistrationRequest registrationRequest = new RegistrationRequest(
      request.get("event").getAsJsonObject().get("eventId").getAsLong(),
      request.get("bundleId").getAsLong(),
      request.get("trackId").isJsonNull() ? null : request.get("trackId").getAsLong(),
      (request.get("danceRoleId") == null || request.get("danceRoleId").isJsonNull()) ? null : request.get("danceRoleId").getAsLong(),
      (request.get("partnerEmail") == null || request.get("partnerEmail").isJsonNull()) ? null : request.get("partnerEmail").getAsString()
    );

    // log.info("inc@updateRegistration, after registrationRequest");
    if (findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      eventService.getCurrentEvent()
    ).isPresent()) {
        // log.info("inc@updateRegistration, update Registration");
        // update Registration
        registration = findByParticipantAndEvent(
          personService.findByUser(userService.findByUserId(userId)),
          eventService.getCurrentEvent()
        ).get();
        updateRegistration(registration, registrationRequest, userId);
      }

    // new Registration
    else {
      try {
        isNewRegistration = true;
        // log.info("inc@updateRegistration, isNewRegistration");
        saveNewRegistration(registrationRequest, userId);
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Base Registration for userId: " + userId + ", request: " + request);
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SAVE_REGISTRATION_EN.getOutTextKey());
      }
      // log.info("inc@updateRegistration, bfr findByParticipantAndEvent");
      registration = findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.getCurrentEvent()
      ).get();
      // log.info("inc@updateRegistration, after findByParticipantAndEvent");
      discountRegistrationService.saveEarlyBird(registration, countAll());
      // log.info("inc@updateRegistration, after early bird");
    }

    // log.info("inc@updateRegistration, bfr food");
    foodRegistrationService.updateFoodRegistrationRequest(registration, request);
    // log.info("inc@updateRegistration, bfr discount");
    discountRegistrationService.updateDiscountRegistrationRequest(registration, request);
    // log.info("inc@updateRegistration, bfr special Reg");
    specialRegistrationService.updateSpecialRegistrationRequest(registration, request);
    // log.info("inc@updateRegistration, bfr privateClass Reg");
    specialRegistrationService.updatePrivateClassRequest(registration, request);
    // log.info("inc@updateRegistration, bfr Host");
    hostingService.updateHostRegistrationRequest(registration, request);
    // log.info("inc@updateRegistration, bfr Hostee");
    hostingService.updateHosteeRegistrationRequest(registration, request);
    // log.info("inc@updateRegistration, bfr Volunteer");
    volunteerService.updateVolunteerRequest(registration, request);
    // log.info("inc@updateRegistration, bfr Scholarship");
    donationRegistrationService.updateScholarshipRequest(registration, request);
    // log.info("inc@updateRegistration, bfr Donation");
    donationRegistrationService.updateDonationRequest(registration, request);
    registrationMatchingService.updateMatchingRequest(registration, registrationRequest);
    if (isNewRegistration) {
      try {
        workflowTransitionService.setWorkflowStatus(
          registration
          ,workflowStatusService.getSubmitted()
        );
        registrationEmailService.sendRegistrationSubmittedEmail(registration);
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Update Workflow Status for userId: " + userId + ", request: " + request);
      }
    }

    return registration;
  }

  public Registration getRegistration(Long userId, Event event) {
    return registrationRepo.findByParticipantAndEventAndActive(
      personService.findByUser(userService.findByUserId(userId)),
      event,
      true
    ).get();
  }
  public RegistrationResponse getRegistrationResponse(Registration registration) {
    RegistrationResponse registrationResponse = new RegistrationResponse(
      registration.getRegistrationId(),
      registration.getParticipant().getUser().getUserId(),
      registration.getEvent(),
      registration.getBundle().getBundleId()
    );

    registrationResponse.setEvent(registration.getEvent());
    registrationResponse.setRegistrationId(registration.getRegistrationId());
    registrationResponse.setBundleId(registration.getBundle().getBundleId());

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
    WorkflowTransition workflowTransition = workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration);
    registrationResponse.setWorkflowStatus(workflowTransition.getWorkflowStatus());

    // Food Registration
    registrationResponse.setFoodRegistrations(
      foodRegistrationService.getAllByRegistration(registration)
    );

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

    // Scholarship Registration
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

    // Private Classes
    registrationResponse.setPrivateClassRegistrations(
      specialRegistrationService.findAllPrivateClassesByRegistration(registration)
    );
    return registrationResponse;
  }

  public RegistrationResponse getRegistrationResponse(Long userId, Long eventId) {
    log.info("getRegistrationResponse");
    Optional<Registration> registrationOptional =
      findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.findByEventId(eventId)
      );
    log.info("registrationOptional.isPresent(): " + registrationOptional.isPresent());
    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      return getRegistrationResponse(registration);
    } else {
      return new RegistrationResponse(userId, eventService.findByEventId(eventId));
    }
  }

  public List<RegistrationResponse> getAllUserRegistrationResponses(Long userId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActive(
        personService.findByUser(userService.findByUserId(userId)), true
      );

    if (registrations != null) {
      registrations.forEach(registration -> {
        registrationResponses.add(getRegistrationResponse(registration));
      });
    }
    return registrationResponses;
  }

  public List<RegistrationResponse> getUserActiveRegistrationResponses(Long userId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActiveAndEventArchivedAndEventActive(
        personService.findByUser(userService.findByUserId(userId)), true
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

  public List<RegistrationResponse> getUserInactiveRegistrationResponses(Long userId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipantAndActiveAndEventArchivedAndEventActive(
        personService.findByUser(userService.findByUserId(userId)), true
        ,false
        ,false
      );

    if (registrations != null) {
      registrations.forEach(registration -> {
        registrationResponses.add(getRegistrationResponse(registration));
      });
    }
    return registrationResponses;
  }

  private void saveNewRegistration(RegistrationRequest request, Long userId) {
    Registration newRegistration = new Registration(
      eventService.findByEventId(request.getEventId()),
      bundleService.findByBundleId(request.getBundleId()),
      personService.findByUser(userService.findByUserId(userId))
    );
    log.info("inc@updateRegistration, saveNewRegistration");
    if (request.getTrackId() != null) {
      newRegistration.setTrack(trackService.findByTrackId(request.getTrackId()));
    }
    log.info("inc@updateRegistration, after save track");
    if (request.getDanceRoleId() != null) {
      newRegistration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    }
    log.info("inc@updateRegistration, after bfr update");
    update(newRegistration);
    log.info("inc@updateRegistration, after after update");
  }

  public Registration findByRegistrationId(Long registrationId){
    return registrationRepo.findByRegistrationId(registrationId);
  }

  private void updateRegistration(Registration registration, RegistrationRequest request, Long userId) {
    registration.setEvent(eventService.findByEventId(request.getEventId()));
    registration.setBundle(bundleService.findByBundleId(request.getBundleId()));
    registration.setParticipant(personService.findByUser(userService.findByUserId(userId)));
    registration.setTrack(trackService.findByTrackId(request.getTrackId()));
    registration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    update(registration);
  }
}
