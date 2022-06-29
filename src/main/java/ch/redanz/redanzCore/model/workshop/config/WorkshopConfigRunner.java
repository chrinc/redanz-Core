package ch.redanz.redanzCore.model.workshop.config;

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
  private final BundleRepo bundleRepo;
  private final TrackRepo trackRepo;
  private final TrackBundleRepo trackBundleRepo;
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;
  private final DanceRoleRepo danceRoleRepo;
  private final TrackDanceRoleRepo trackDanceRoleRepo;
  private final WorkflowStatusRepo workflowStatusRepo;
  private final LanguageRepo languageRepo;
  private final OutTextRepo outTextRepo;
  private final TypeSlotRepo typeSlotRepo;
  private final SlotRepo slotRepo;
  private final FoodRepo foodRepo;
  private final SleepUtilRepository sleepUtilRepository;
  private final TrackDiscountRepo trackDiscountRepo;
  private final DiscountRepo discountRepo;

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
      languageRepo.saveAll(LanguageConfig.setup());
      outTextRepo.saveAll(OutTextConfig.setup(languageRepo));
      slotRepo.saveAll(SlotConfig.setup());
      foodRepo.saveAll(FoodConfig.setup());
      typeSlotRepo.saveAll(TypeSlotConfig.setup(slotRepo, foodRepo));
      sleepUtilRepository.saveAll(SleepUtilConfig.setup());
      discountRepo.saveAll(DiscountConfig.setup());
      trackDiscountRepo.saveAll(TrackDiscountConfig.setup(trackRepo, discountRepo));
    }
  }
}
