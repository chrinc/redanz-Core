package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.repository.*;
import ch.redanz.redanzCore.model.workshop.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(200)
public class WorkshopConfigRunner implements CommandLineRunner {
  private final UserRepo userRepo;
  private final BundleRepo bundleRepo;
  private final TrackRepo trackRepo;
  private final TrackBundleRepo trackBundleRepo;
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;
  private final DanceRoleRepo danceRoleRepo;
  private final TrackDanceRoleRepo trackDanceRoleRepo;
  private final WorkflowStatusRepo workflowStatusRepo;
  private final PersonRepo personRepo;
  private final RegistrationRepo registrationRepo;
  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowTransitionRepo workflowTransitionRepo;

  @Override
  public void run(String... args) throws Exception {
    /*
      @todo later: implement controller and run via rest-call
                    for now, we initiate when starting teh server
                    for the first time
    */
    if (eventRepo.findAll().isEmpty()) {
      trackRepo.saveAll(TrackConfig.setup());
      bundleRepo.saveAll(BundleConfig.setup());
      trackBundleRepo.saveAll(BundleTrackConfig.setup(trackRepo, bundleRepo));
      eventRepo.saveAll(EventConfig.setup());
      bundleEventRepo.saveAll(EventBundleConfig.setup(bundleRepo, eventRepo));
      danceRoleRepo.saveAll(DanceRoleConfig.setup());
      trackDanceRoleRepo.saveAll(TrackDanceRoleConfig.setup(trackRepo, danceRoleRepo));
      workflowStatusRepo.saveAll(WorkflowStatusConfig.setup());
    }
  }
}
