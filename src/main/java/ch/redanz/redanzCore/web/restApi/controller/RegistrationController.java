package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.response.RegistrationRequest;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("core-api/app/register")
@AllArgsConstructor
public class RegistrationController {
  private final RegistrationService registrationService;
  private final TrackService trackService;
  private final BundleService bundleService;
  private final EventService eventService;
  private final PersonService personService;
  private final RegistrationMatchingService registrationMatchingService;
  private final UserService userService;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;
  private final DanceRoleService danceRoleService;
  private final RegistrationRepo registrationRepo;

  @Autowired
  private Environment environment;

  @Autowired
  Configuration mailConfig;

  @GetMapping(path="/event/all")
  public List<Event> getAllEvents() {
    log.info("inc, send getAllTracks: {}.", registrationService.getAllEvents());
    return registrationService.getAllEvents();
  }

  @GetMapping(path="/event/current")
  public Event getCurrentEvent() {
    log.info("inc, send getAllTracks: {}.", registrationService.getCurrentEvent());
    return registrationService.getCurrentEvent();
  }
  @GetMapping(path="/workflow_status_list")
  public List<WorkflowStatus> getWorkflowStatusList() {
    log.info("inc, send workflow status: {}.", registrationService.getWorkflowStatusList());
    List<WorkflowStatus> workflowStatusList = registrationService.getWorkflowStatusList();
    workflowStatusList.remove(
       workflowStatusList.indexOf(
          workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.CANCELLED.getName())
       )
    );
    return workflowStatusList;
  }

  @GetMapping(path="/registration")
  @Transactional
  public ch.redanz.redanzCore.model.registration.response.RegistrationResponse getRegistration(
          @RequestParam("userId") Long userId,
          @RequestParam("eventId") Long eventId
  ) {

    Optional<Registration> registrationOptional =
            registrationService.findByParticipantAndEvent(
                    personService.findByUser(userService.findByUserId(userId)),
                    eventService.findByEventId(eventId)
            );

    log.info("inc find registration");
    if (registrationOptional.isPresent()) {
      Registration registration = registrationOptional.get();
      ch.redanz.redanzCore.model.registration.response.RegistrationResponse registrationResponse = new ch.redanz.redanzCore.model.registration.response.RegistrationResponse(
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

      return registrationResponse;
    }
    return null;
  }

  @PostMapping(path="/submit")
  @Transactional
  public void register(
          @RequestParam("userId") Long userId,
          @RequestBody RegistrationRequest registrationRequest
  ) throws IOException, TemplateException {
//    log.info("inc@submit, loginlink {}", loginLink);
    new RegistrationService(
      eventService,
      workflowStatusService,
      workflowTransitionService,
      registrationRepo,
      bundleService,
      trackService,
      danceRoleService,
      personService,
      userService,
      registrationMatchingService,
      mailConfig
    ).submitRegistration(userId, registrationRequest, environment.getProperty("link.login"));
  }
}
