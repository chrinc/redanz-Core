package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.jobs.EODMatchingJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReleaseJob;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(800)
@Profile("dev || test")
public class RegistrationTestConfigRunner implements CommandLineRunner {
  private final UserService userService;
  private final BundleService bundleService;
  private final TrackService trackService;
  private final EventService eventService;
  private final DanceRoleService danceRoleService;
  private final WorkflowStatusService workflowStatusService;
  private final PersonService personService;
  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationEmailService registrationEmailService;
  private final EODMatchingJob eodMatchingJob;
  private final EODReleaseJob eodReleaseJob;
  private final RegistrationService registrationService;
  private final WorkflowTransitionService workflowTransitionService;

  @Autowired
  Configuration mailConfig;

  @Override
  public void run(String... args) throws Exception {
    RegistrationConfig.setup(
      personService, registrationService, bundleService, trackService, danceRoleService, userService, eventService, registrationEmailService
    );
    // loads test data
//    registrationEmailRepo.saveAll(setupEmailRegistration(registrationList));

    RegistrationMatchingConfig.setup(
      eventService, registrationService, personService, userService, registrationMatchingService
    );

    WorkflowTransitionConfig.setup(
      eventService, registrationService, personService, userService, workflowStatusService, WorkflowStatusConfig.SUBMITTED,
      workflowTransitionService
    );
    // run EOD Scheduler first time
    Thread.sleep(1000);
    eodMatchingJob.runMatching();
    eodReleaseJob.runRelease();
    Thread.sleep(1000);
    WorkflowTransitionConfig.setup(
      eventService, registrationService, personService, userService, workflowStatusService, WorkflowStatusConfig.DONE, workflowTransitionService
    );
  }
}
