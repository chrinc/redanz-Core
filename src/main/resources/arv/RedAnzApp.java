//package ch.redanz.redanzCore.model.config;
//
//import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
//import ch.redanz.redanzCore.model.profile.repository.UserRepo;
//import ch.redanz.redanzCore.model.registration.repository.*;
//import ch.redanz.redanzCore.model.registration.RegistrationMatching;
//import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
//import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
//import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//@Slf4j
//@AllArgsConstructor
//public class RedAnzApp implements CommandLineRunner {
//  private final UserRepo userRepo;
//  private final BundleRepo bundleRepo;
//  private final TrackRepo trackRepo;
//  private final TrackBundleRepo trackBundleRepo;
//  private final EventRepo eventRepo;
//  private final BundleEventRepo bundleEventRepo;
//  private final DanceRoleRepo danceRoleRepo;
//  private final TrackDanceRoleRepo trackDanceRoleRepo;
//  private final WorkflowStatusRepo workflowStatusRepo;
//  private final PersonRepo personRepo;
//  private final RegistrationRepo registrationRepo;
//  private final RegistrationMatchingRepo registrationMatchingRepo;
//  private final WorkflowTransitionRepo workflowTransitionRepo;
//
//
//  private final RegistrationMatchingService registrationMatchingService;
//  private final WorkflowTransitionService workflowTransitionService;
//  private final WorkflowStatusService workflowStatusService;
//  private final Map<RegistrationMatching, RegistrationMatching> matchingPairs;
//
//  @Override
//  public void run(String... args) throws Exception {
//    log.info("inc@RedAnzApp");
////
////    trackRepo.saveAll(TrackConfig.setup());
////    bundleRepo.saveAll(BundleConfig.setup());
////    trackBundleRepo.saveAll(BundleTrackConfig.setup(trackRepo, bundleRepo));
////    eventRepo.saveAll(EventConfig.setup());
////    bundleEventRepo.saveAll(EventBundleConfig.setup(bundleRepo, eventRepo));
////    danceRoleRepo.saveAll(DanceRoleConfig.setup());
////    trackDanceRoleRepo.saveAll(TrackDanceRoleConfig.setup(trackRepo, danceRoleRepo));
////    workflowStatusRepo.saveAll(WorkflowStatusConfig.setup());
//
//    // Testings
////    userRepo.saveAll(UserConfig.setup());
////    personRepo.saveAll(PersonConfig.setup(userRepo));
////    registrationRepo.saveAll(RegistrationConfig.setup(
////      personRepo, eventRepo, bundleRepo, trackRepo, danceRoleRepo, userRepo
////    ));
////    registrationMatchingRepo.saveAll(RegistrationMatchingConfig.setup(
////      eventRepo, registrationRepo, personRepo, userRepo
////    ));
////    workflowTransitionRepo.saveAll(WorkflowTransitionConfig.setup(
////      eventRepo, registrationRepo, personRepo, userRepo, workflowStatusRepo
////    ));
//
//    // run EOD Scheduler first time
////    EODScheduler eodScheduler = new EODScheduler(
////      registrationMatchingService,
////      workflowTransitionService,
////      workflowStatusService,
////      matchingPairs
////    );
////    eodScheduler.matchingJob();
//  }
//}
