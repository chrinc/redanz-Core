package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import com.google.gson.JsonParser;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("core-api/app/register")
@AllArgsConstructor
public class RegistrationController {
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final PersonService personService;
  private final RegistrationMatchingService registrationMatchingService;
  private final UserService userService;
  private final WorkflowStatusService workflowStatusService;
  private final WorkflowTransitionService workflowTransitionService;


  @Autowired
  private Environment environment;

  @Autowired
  Configuration mailConfig;

  @GetMapping(path="/registration")
  @Transactional
  public RegistrationResponse getRegistration(
          @RequestParam("userId") Long userId,
          @RequestParam("eventId") Long eventId
  ) {

    Optional<Registration> registrationOptional =
            registrationService.findByParticipantAndEvent(
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
              registrationService.getFoodRegistrations(registration)
      );

      log.info("inc bfr host Registration");
      // Host Registration
      registrationResponse.setHostRegistration(
              registrationService.getHostRegistrations(registration)
      );

      // Hostee Registration
      registrationResponse.setHosteeRegistration(
              registrationService.getHosteeRegistrations(registration)
      );

      // Volunteer Registration
      registrationResponse.setVolunteerRegistration(
              registrationService.getVolunteerRegistration(registration)
      );

      log.info("inc get scholarship registration");
      // Scholarship Registration
      registrationResponse.setScholarshipRegistration(
              registrationService.getScholarshipRegistration(registration)
      );


      log.info("inc getDonationRegistration");
      // Scholarship Registration
      registrationResponse.setDonationRegistration(
        registrationService.getDonationRegistration(registration)
      );

      return registrationResponse;
    }
    return null;
  }

  @PostMapping(path="/submit")
  @Transactional
  public void register(
          @RequestParam("userId") Long userId,
          @RequestBody String jsonObject
  ) throws IOException, TemplateException {
    log.info("inc, userId: {}", userId);
    registrationService.submitRegistration(
      userId,
      JsonParser.parseString(jsonObject).getAsJsonObject(),
      environment.getProperty("link.login")
    );
}

  @GetMapping(path="/workflow/status/all")
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
}
