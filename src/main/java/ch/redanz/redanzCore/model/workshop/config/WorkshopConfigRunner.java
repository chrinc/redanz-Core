package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.configTest.BaseParConfig;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.configTest.TypeSlotConfig;
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
@Profile("redanz-prod || redanz-atest || redanz-stest || redanz-rdev|| stirit-prod || stirit-atest || stirit-stest || stirit-rdev")
public class WorkshopConfigRunner implements CommandLineRunner {
  private final BaseParService baseParService;
  private final OutTextService outTextService;
  private final SlotService slotService;
  private final FoodService foodService;
  private final LanguageService languageService;
  private final DanceRoleService danceRoleService;
  private final EventPartService eventPartService;

  @Override
  public void run(String... args) throws Exception {
    BaseParConfig.setup(baseParService);
    OutTextConfig.setup(outTextService);
    SlotConfig.setup(slotService);
    TypeSlotConfig.setup(slotService, foodService, outTextService, languageService);
    DanceRoleConfig.setup(danceRoleService);
    EventPartConfig.setup(eventPartService);
  }
}
