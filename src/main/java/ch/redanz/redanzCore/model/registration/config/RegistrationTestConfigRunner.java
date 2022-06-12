package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.registration.jobs.EODMatchingJob;
import ch.redanz.redanzCore.model.registration.jobs.EODReleaseJob;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import freemarker.template.Configuration;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
@Order(800)
@Profile("dev || test")
public class RegistrationTestConfigRunner implements CommandLineRunner {
  private final UserRepo userRepo;
  private final BundleRepo bundleRepo;
  private final TrackRepo trackRepo;
  private final EventRepo eventRepo;
  private final DanceRoleRepo danceRoleRepo;
  private final WorkflowStatusRepo workflowStatusRepo;
  private final PersonRepo personRepo;
  private final RegistrationRepo registrationRepo;
  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowTransitionRepo workflowTransitionRepo;

  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationService registrationService;
  private final WorkflowTransitionService workflowTransitionService;
  private final WorkflowStatusService workflowStatusService;
  private final Map<RegistrationMatching, RegistrationMatching> matchingPairs;


  @Autowired
  Configuration mailConfig;

  @Autowired
  private Environment environment;


  @Override
  public void run(String... args) throws Exception {

    // loads test data
    registrationRepo.saveAll(RegistrationConfig.setup(
      personRepo, eventRepo, bundleRepo, trackRepo, danceRoleRepo, userRepo
    ));
    registrationMatchingRepo.saveAll(RegistrationMatchingConfig.setup(
      eventRepo, registrationRepo, personRepo, userRepo
    ));
    workflowTransitionRepo.saveAll(WorkflowTransitionConfig.setup(
      eventRepo, registrationRepo, personRepo, userRepo, workflowStatusRepo, WorkflowStatusConfig.SUBMITTED
    ));

    // run EOD Scheduler first time
    Thread.sleep(1000);
    EODMatchingJob eodMatchingJob = new EODMatchingJob(
      registrationMatchingService,
      workflowTransitionService,
      matchingPairs
    );
    eodMatchingJob.runMatching();
    EODReleaseJob eodReleaseJob = new EODReleaseJob(
      registrationMatchingService,
      workflowTransitionService,
      workflowStatusService,
      registrationService,
      environment,
      mailConfig
    );
    eodReleaseJob.runRelease();
    Thread.sleep(1000);
    workflowTransitionRepo.saveAll(WorkflowTransitionConfig.setup(
      eventRepo, registrationRepo, personRepo, userRepo, workflowStatusRepo, WorkflowStatusConfig.DONE
    ));

  }
}