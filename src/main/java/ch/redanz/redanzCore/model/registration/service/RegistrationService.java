package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.response.RegistrationRequest;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import ch.redanz.redanzCore.service.email.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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

  @Autowired
  private final Configuration mailConfig;
//  String loginLink;


  public List<Event> getAllEvents() { return eventService.findAll(); }
  public List<Registration> findAll() {return registrationRepo.findAll(); }
  public Event getCurrentEvent() { return eventService.findByName(EventConfig.EVENT2022.getName());}
  public List<WorkflowStatus> getWorkflowStatusList() {return workflowStatusService.findAll();}
  public void register(Registration registration) { registrationRepo.save(registration);}
  public Optional<Registration> findByParticipantAndEvent(Person participant, Event event) {
    return registrationRepo.findByParticipantAndEvent(participant, event);
  }

  public List<Registration> getAllSubmittedRegistrations(){
    List<Registration> submittedRegistrations = new ArrayList<>();

    registrationRepo.findAllByEvent(
      eventService.findByName(EventConfig.EVENT2022.getName())
    ).forEach(registration -> {
      log.info("inc@registrationService, registration of {}", registration.getParticipant().getFirstName());
      if (workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration)
        .getWorkflowStatus().getName()
        .equals(WorkflowStatusConfig.SUBMITTED.getName()
        )
      ) {
        submittedRegistrations.add(registration);
      }
    });
    return submittedRegistrations;
  }

  public void submitRegistration(Long userId, RegistrationRequest request, String link) throws IOException, TemplateException {
    // new Registration
    Registration newRegistration = new Registration(
      eventService.findByEventId(request.getEventId()),
      bundleService.findByBundleId(request.getBundleId()),
      personService.findByUser(userService.findByUserId(userId))
    );

    if (request.getTrackId() != null) {
      newRegistration.setTrack(trackService.findByTrackId(request.getTrackId()));
    }

    if (request.getDanceRoleId() != null ) {
      newRegistration.setDanceRole(danceRoleService.findByDanceRoleId(request.getDanceRoleId()));
    }

    registrationRepo.save(newRegistration);

    // Workflow Status
    WorkflowTransition workflowTransition = new WorkflowTransition(
      workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.SUBMITTED.getName()),
      newRegistration,
      LocalDateTime.now()
    );

    workflowTransitionService.saveWorkflowTransition(workflowTransition);

    // Registration Matching
    if (request.getTrackId() != null && newRegistration.getTrack().getPartnerRequired()) {
      RegistrationMatching registrationMatching
        = new RegistrationMatching(newRegistration);

      if (request.getPartnerEmail() != null) {
        registrationMatching.setPartnerEmail(request.getPartnerEmail());
      }
      registrationMatchingService.save(registrationMatching);
    }

    Map<String, Object> model = new HashMap<>();
    model.put("link", link);
    model.put("firstName", newRegistration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationSubmitted.ftl");

//    EmailService emailService = new EmailService();
    EmailService.sendEmail(
      EmailService.getSession(),
      userService.findByUserId(userId).getEmail(),
      "Registration Submitted",
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }
}
