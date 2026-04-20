package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.workshop.configTest.LanguageConfig;
import ch.redanz.redanzCore.model.workshop.configTest.SleepUtilConfig;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
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
@Profile("stirit-prod || stirit-atest || stirit-stest || qsxz-prod")
public class WorkshopConfigRunner implements CommandLineRunner {
  private final OutTextService outTextService;
  private final SlotService slotService;
  private final FoodService foodService;
  private final LanguageService languageService;
  private final DanceRoleService danceRoleService;
  private final EventPartService eventPartService;
  private final WorkflowStatusService workflowStatusService;
  private final CountryService countryService;
  private final SleepUtilService sleepUtilService;
  private final EventService eventService;

  @Override
  public void run(String... args) throws Exception {
    OutTextConfig.setup(outTextService);

    WorkflowStatusConfig.setup(workflowStatusService);
    LanguageConfig.setup(languageService);
    CountryConfig.setup(countryService, languageService);
    DanceRoleConfig.setup(danceRoleService);
    SleepUtilConfig.setup(sleepUtilService);

    DanceRoleConfig.setup(danceRoleService);
    EventPartConfig.setup(eventPartService);
  }
}
