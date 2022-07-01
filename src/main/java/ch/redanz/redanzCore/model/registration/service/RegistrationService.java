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
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

  public void update(Registration registration) {
    registrationRepo.save(registration);
  }

  public void updateSoldOut(){

    // Event
    Event currentEvent = eventService.getCurrentEvent();

    log.info("inc@SoldOut?, eventCapa: {}", currentEvent.getCapacity());
    log.info("inc@SoldOut?, countReleasedAndDone(): {}", countReleasedAndDone());
    if (countReleasedAndDone() >= currentEvent.getCapacity()) {
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

      log.info("inc@SoldOut?, bundleName: {}", bundle.getName());
      log.info("inc@SoldOut?, bundleCapa: {}", bundle.getCapacity());
      log.info("inc@SoldOut?, countBundlesReleasedAndDone(): {}", countBundlesReleasedAndDone(bundle));
      if (countBundlesReleasedAndDone(bundle) >= bundle.getCapacity()) {
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

          log.info("inc@SoldOut?, trackName: {}", track.getName());
          log.info("inc@SoldOut?, trackCapa: {}", track.getCapacity());
          log.info("inc@SoldOut?, countBundlesReleasedAndDone(): {}", countTracksReleasedAndDone(track));
          if (countTracksReleasedAndDone(track) >= track.getCapacity()) {
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

  public List<WorkflowStatus> getWorkflowStatusList() {
    return workflowStatusService.findAll();
  }

  public int countReleasedAndDone() {
    return registrationRepo.countAllByWorkflowStatusAndEvent(
      workflowStatusService.getConfirming(), eventService.getCurrentEvent()
    )
      +
      registrationRepo.countAllByWorkflowStatusAndEvent(
      workflowStatusService.getDone(), eventService.getCurrentEvent()
    );
  }

  public int countBundlesReleasedAndDone(Bundle bundle) {
    return
      registrationRepo.countAllByBundleAndWorkflowStatusAndEvent(
        bundle, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
      )
        +
        registrationRepo.countAllByBundleAndWorkflowStatusAndEvent(
          bundle, workflowStatusService.getDone(), eventService.getCurrentEvent()
        );
  }
  public int countTracksReleasedAndDone(Track track) {
    if (track == null) return 0;

    return
      registrationRepo.countAllByTrackAndWorkflowStatusAndEvent(
        track, workflowStatusService.getConfirming(), eventService.getCurrentEvent()
      )
        +
        registrationRepo.countAllByTrackAndWorkflowStatusAndEvent(
          track, workflowStatusService.getDone(), eventService.getCurrentEvent()
        );
  }
  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event) {
    return registrationRepo.findByParticipantAndEvent(participant, event);
  }

  public List<Registration> findAllByCurrentEvent() {
    return registrationRepo.findAllByEvent(eventService.getCurrentEvent());
  }

  public List<Registration> findAllByCurrentEventAndWorkflowStatus(WorkflowStatus workflowStatus) {
    return registrationRepo.findAllByWorkflowStatusAndEvent(
      workflowStatus,
      eventService.getCurrentEvent()
    );
  }

  public List<Registration> getAllConfirmingRegistrations() {
    return getAllByWorkflowStatus(
      workflowStatusService.getConfirming()
    );
  }

  public List<Registration> getAllDoneRegistrations() {
    return getAllByWorkflowStatus(
      workflowStatusService.getDone()
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
  public void submitRegistration(Long userId, JsonObject request) throws IOException, TemplateException {
    log.info("request: {}", request);
    RegistrationRequest registrationRequest = new RegistrationRequest(
      request.get("eventId").getAsLong(),
      request.get("bundleId").getAsLong(),
      request.get("trackId") == null ? null : request.get("trackId").getAsLong(),
      request.get("danceRoleId") == null ? null : request.get("danceRoleId").getAsLong(),
      request.get("partnerEmail") == null ? null : request.get("partnerEmail").getAsString()
    );

    saveNewRegistration(registrationRequest, userId);
    Registration registration = findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      eventService.getCurrentEvent()
    ).get();

    if (!request.get("foodRegistration").getAsJsonArray().isEmpty()) {
      foodRegistrationService.saveFoodRegistration(registration, request.get("foodRegistration").getAsJsonArray());
    }
    if (!request.get("discountRegistration").getAsJsonArray().isEmpty()) {
      discountRegistrationService.saveDiscountRegistration(registration, request.get("discountRegistration").getAsJsonArray());
    }
    if (!request.get("hostRegistration").getAsJsonArray().isEmpty()) {
      hostingService.saveHostRegistration(registration, request.get("hostRegistration").getAsJsonArray());
    }
    if (!request.get("hosteeRegistration").getAsJsonArray().isEmpty()) {
      hostingService.saveHosteeRegistration(registration, request.get("hosteeRegistration").getAsJsonArray());
    }
    if (request.get("volunteerRegistration") != null) {
      volunteerService.saveVolunteerRegistration(registration, request.get("volunteerRegistration").getAsJsonObject());
    }
    if (request.get("scholarshipRegistration") != null) {
      donationRegistrationService.saveScholarishpRegistration(registration, request.get("scholarshipRegistration").getAsJsonArray());
    }
    if (!request.get("donationRegistration").isJsonNull()) {
      donationRegistrationService.saveDonationRegistration(registration, request.get("donationRegistration").getAsJsonArray());
    }
    workflowTransitionService.setWorkflowStatus(
      registration
      , workflowStatusService.getSubmitted()
    );
    registrationMatchingService.setRegistrationMatching(registration, registrationRequest);
    registrationEmailService.sendRegistrationSubmittedEmail(registration);
  }

  public Registration getRegistration(Long userId, Event event) {
    return registrationRepo.findByParticipantAndEvent(
      personService.findByUser(userService.findByUserId(userId)),
      event
    ).get();
  }

  public RegistrationResponse getRegistrationResponse(Long userId, Long eventId) {
    Optional<Registration> registrationOptional =
      findByParticipantAndEvent(
        personService.findByUser(userService.findByUserId(userId)),
        eventService.findByEventId(eventId)
      );

    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      RegistrationResponse registrationResponse = new RegistrationResponse(
        registration.getRegistrationId(),
        registration.getParticipant().getUser().getUserId(),
        registration.getEvent().getEventId(),
        registration.getBundle().getBundleId()
      );

      registrationResponse.setEventId(registration.getEvent().getEventId());
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
        hostingService.getHostRegistrations(registration)
      );

      // Hostee Registration
      registrationResponse.setHosteeRegistration(
        hostingService.getHosteeRegistrations(registration)
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

      registrationResponse.setDiscountRegistrations(
        discountRegistrationService.findAllByRegistration(registration)
      );
      return registrationResponse;
    }
    return null;
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
}
