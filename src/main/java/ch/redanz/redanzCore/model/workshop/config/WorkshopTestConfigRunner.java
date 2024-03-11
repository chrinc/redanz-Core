package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(200)
@Profile("rdev || dev")
public class WorkshopTestConfigRunner implements CommandLineRunner {
  private final BundleService bundleService;
  private final TrackService trackService;
  private final EventService eventService;
  private final DanceRoleService danceRoleService;
  private final WorkflowStatusService workflowStatusService;
  private final LanguageService languageService;
  private final OutTextService outTextService;
  private final SlotService slotService;
  private final FoodService foodService;
  private final SleepUtilService sleepUtilService;
  private final DiscountService discountService;
  private final SpecialService specialService;
  private final VolunteerService volunteerService;
  private final BaseParService baseParService;
  private final CountryService countryService;
  private final PrivateClassService privateClassService;

  @Override
  public void run(String... args) {
    if (eventService.findAll().isEmpty()) {
      TrackConfig.setup(trackService);
      BundleConfig.setup(bundleService, slotService);
      BundleTrackConfig.setup(bundleService, trackService);
      EventConfig.setup(eventService);
      EventBundleConfig.setup(bundleService, eventService);
      DanceRoleConfig.setup(danceRoleService);
      TrackDanceRoleConfig.setup(trackService, danceRoleService);
      WorkflowStatusConfig.setup(workflowStatusService);
      LanguageConfig.setup(languageService);
      OutTextConfig.setup(outTextService);
      SlotConfig.setup(slotService);
      FoodConfig.setup(foodService);
      TypeSlotConfig.setup(slotService, foodService);
      SleepUtilConfig.setup(sleepUtilService);
      DiscountConfig.setup(discountService);
      TrackDiscountConfig.setup(trackService, discountService);
      EventTypeSlotConfig.setup(slotService, eventService, foodService);
      SpecialConfig.setup(specialService);
      EventDiscountConfig.setup(discountService, eventService);
      VolunteerTypeConfig.setup(volunteerService);
      EventVolunteerTypeConfig.setup(eventService, volunteerService);
      BaseParConfig.setup(baseParService);
      CountryConfig.setup(countryService, languageService);

      PrivateClassConfig.setup(privateClassService);
      EventPrivateClassConfig.setup(privateClassService, eventService);
      EventSpecialsConfig.setup(specialService, eventService);
      BundleSpecialConfig.setup(specialService, bundleService);
    }
  }
}
