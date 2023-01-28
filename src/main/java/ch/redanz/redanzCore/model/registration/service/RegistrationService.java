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
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
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
  private final ErrorLogService errorLogService;

  public void update(Registration registration) {
    registrationRepo.save(registration);
  }

  public void updateSoldOut(){

    // Event
    Event currentEvent = eventService.getCurrentEvent();
    if (countDoneByEvent(currentEvent) >= currentEvent.getCapacity()) {
      if (!currentEvent.isSoldOut()) {
        currentEvent.setSoldOut(true);
        eventService.save(currentEvent);
      }
    } else {
      if (currentEvent.isSoldOut()) {
        currentEvent.setSoldOut(false);
        eventService.save(currentEvent);
      }
    }

    // Bundle
    currentEvent.getEventBundles().forEach(eventBundle -> {
      Bundle bundle = eventBundle.getBundle();
      if (countBundlesDone(bundle) >= bundle.getCapacity()) {
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

      // Tracks
      if (!bundle.getBundleTracks().isEmpty()) {
        bundle.getBundleTracks().forEach(bundleTrack -> {
          Track track = bundleTrack.getTrack();
          if (countTracksDone(track) >= track.getCapacity()) {
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
  }

  public List<Registration> findAllCurrentEvent() {
    return registrationRepo.findAllByEvent(eventService.getCurrentEvent());
  }

  public List<Registration> findAllByEvent(Event event) {
    return registrationRepo.findAllByEvent(event);
  }

  public List<WorkflowStatus> getWorkflowStatusList() {
    return workflowStatusService.findAll();
  }

  public int countAll() {
    return registrationRepo.countAllByEvent(
      eventService.getCurrentEvent()
    );
  }
  public int countSubmittedConfirmingAndDoneByEvent(Event event) {
    return countSubmittedByEvent(event) + countConfirmingByEvent(event) + countDoneByEvent(event);
  }
  public int countConfirmingAndDoneByEvent(Event event) {
    return countConfirmingByEvent(event) + countDoneByEvent(event);
  }
  public int countSubmittedByEvent(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndEvent(
      workflowStatusService.getSubmitted(), event
    );
  }
  public int countConfirmingByEvent(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndEvent(
      workflowStatusService.getConfirming(), event
    );
  }
  public int countDoneByEvent(Event event) {
    return registrationRepo.countAllByWorkflowStatusAndEvent(
      workflowStatusService.getDone(), event
    );
  }

  private int countBundlesSubmitted(Bundle bundle) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndEvent(
        bundle, workflowStatusService.getSubmitted(), eventService.getCurrentEvent()
      );
  }
  private int countBundlesConfirming(Bundle bundle) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndEvent(
        bundle, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
      );
  }
  public int countBundlesDone(Bundle bundle) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndEvent(
        bundle, workflowStatusService.getDone(), eventService.getCurrentEvent()
      );
  }
  public int countBundlesConfirmingAndDone(Bundle bundle) {
    return countBundlesConfirming(bundle) + countBundlesDone(bundle);
  }
  public int countBundlesSubmittedConfirmingAndDone(Bundle bundle) {
    return countBundlesConfirming(bundle) + countBundlesDone(bundle) + countBundlesSubmitted(bundle);
  }
  private int countTracksConfirming(Track track) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndEvent(
        track, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
      );
  }
  private int countTracksSubmitted(Track track) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndEvent(
        track, workflowStatusService.getSubmitted(), eventService.getCurrentEvent()
      );
  }
  public int countTracksDone(Track track) {
    if (track == null) return 0;
    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndEvent(
        track, workflowStatusService.getDone(), eventService.getCurrentEvent()
      );
  }
  public int countTracksConfirmingAndDone(Track track) {
    if (track == null) return 0;
    return countTracksDone(track) + countTracksConfirming(track);
  }

  public int countTracksSubmittedConfirmingAndDone(Track track) {
    if (track == null) return 0;
    return countTracksDone(track) + countTracksConfirming(track) + countTracksSubmitted(track);
  }

  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event) {
    return registrationRepo.findByParticipantAndEvent(participant, event);
  }

  public void onCancel(Registration registration) throws TemplateException, IOException {
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getCancelled()
    );
    updateSoldOut();
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
    updateSoldOut();
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
    return registrationRepo.findAllByWorkflowStatusAndEvent(
      workflowStatus,
      eventService.findByName(EventConfig.EVENT2022.getName())
    );
  }

  @Transactional
  public void updateRegistrationRequest(Long userId, JsonObject request) throws IOException, TemplateException {
    log.info("inc@updateRegistration, userId: {}", userId);
    log.info("inc@updateRegistration, request: {}", request);
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

    log.info("inc@updateRegistration, after registrationRequest");
    if (findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      eventService.getCurrentEvent()
    ).isPresent())

    // update Registration
    {
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
        saveNewRegistration(registrationRequest, userId);
      } catch (Exception exception) {
        errorLogService.addLog(ErrorLogType.SUBMIT_REGISTRATION.toString(), "Base Registration for userId: " + userId + ", request: " + request);
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SAVE_REGISTRATION_EN.getOutTextKey());
      }
      registration = findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.getCurrentEvent()
      ).get();
      discountRegistrationService.saveEarlyBird(registration, countAll());
    }

    log.info("inc@updateRegistration, bfr food");
    foodRegistrationService.updateFoodRegistrationRequest(registration, request);
    log.info("inc@updateRegistration, bfr discount");
    discountRegistrationService.updateDiscountRegistrationRequest(registration, request);
    log.info("inc@updateRegistration, bfr special Reg");
    specialRegistrationService.updateSpecialRegistrationRequest(registration, request);
    log.info("inc@updateRegistration, bfr Host");
    hostingService.updateHostRegistrationRequest(registration, request);
    log.info("inc@updateRegistration, bfr Hostee");
    hostingService.updateHosteeRegistrationRequest(registration, request);
    log.info("inc@updateRegistration, bfr Volunteer");
    volunteerService.updateVolunteerRequest(registration, request);
    log.info("inc@updateRegistration, bfr Scholarship");
    donationRegistrationService.updateScholarshipRequest(registration, request);
    log.info("inc@updateRegistration, bfr Donation");
    donationRegistrationService.updateDonationRequest(registration, request);
    log.info("inc@updateRegistration, bfr Matching");
    registrationMatchingService.updateMatchingRequest(registration, registrationRequest);

    log.info("inc@updateRegistration, isNewRegistration: " + isNewRegistration);
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

  }

  public Registration getRegistration(Long userId, Event event) {
    return registrationRepo.findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      event
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
    return registrationResponse;
  }

  public RegistrationResponse getRegistrationResponse(Long userId, Long eventId) {
    Optional<Registration> registrationOptional =
      findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.findByEventId(eventId)
      );
    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      return getRegistrationResponse(registration);
    } else {
      return null;
    }
  }

  public List<RegistrationResponse> getAllUserRegistrationResponses(Long userId) {
    List<RegistrationResponse> registrationResponses = new ArrayList<>();
    List<Registration> registrations =
      registrationRepo.findAllByParticipant(
        personService.findByUser(userService.findByUserId(userId))
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
      registrationRepo.findAllByParticipantAndEventArchivedAndEventActive(
        personService.findByUser(userService.findByUserId(userId))
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
      registrationRepo.findAllByParticipantAndEventArchivedAndEventActive(
        personService.findByUser(userService.findByUserId(userId))
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
    if (request.getTrackId() != null) {
      newRegistration.setTrack(trackService.findByTrackId(request.getTrackId()));
    }
    if (request.getDanceRoleId() != null) {
      newRegistration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    }
    update(newRegistration);
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
