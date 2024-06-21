package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.workshop.config.*;
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
  private final EventPartService eventPartService;

  @Override
  public void run(String... args) {
    if (eventService.findAll().isEmpty()) {
      OutTextConfig.setup(outTextService);
      WorkflowStatusConfig.setup(workflowStatusService);
      LanguageConfig.setup(languageService);
      CountryConfig.setup(countryService, languageService);
      DanceRoleConfig.setup(danceRoleService);
      SleepUtilConfig.setup(sleepUtilService);
      SlotConfig.setup(slotService);

      EventConfig.setup(eventService, discountService);
      TrackConfig.setup(trackService);
      EventTrackConfig.setup(eventService, trackService);
      DiscountConfig.setup(discountService);
      EventDiscountConfig.setup(discountService, eventService);
      TrackEventDiscountConfig.setup(eventService, trackService, discountService);
      TrackDanceRoleConfig.setup(trackService, danceRoleService);

      SpecialConfig.setup(specialService);
      EventSpecialsConfig.setup(specialService, eventService);

      BundleConfig.setup(bundleService, slotService);
      EventBundleConfig.setup(bundleService, eventService);
      BundleEventSpecialConfig.setup(eventService, specialService, bundleService);
      BundleEventTrackConfig.setup(eventService, trackService, bundleService);
      BundlePartySlotConfig.setup(slotService, bundleService);

      PrivateClassConfig.setup(privateClassService);
      EventPrivateClassConfig.setup(privateClassService, eventService);

      FoodConfig.setup(foodService);
      EventFoodSlotConfig.setup(eventService, foodService, slotService);
      TypeSlotConfig.setup(slotService, foodService, outTextService, languageService);
      SleepUtilConfig.setup(sleepUtilService);

      EventTypeSlotConfig.setup(slotService, eventService, foodService);
      VolunteerTypeConfig.setup(volunteerService);
      EventVolunteerTypeConfig.setup(eventService, volunteerService);
      EventPartConfig.setup(eventPartService);
      EventPartInfoConfig.setup(eventService, eventPartService);

      BaseParConfig.setup(baseParService);
    }
  }
}
