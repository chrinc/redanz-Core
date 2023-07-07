package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
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
@Profile("prod")
public class WorkshopConfigRunner implements CommandLineRunner {
  private final OutTextService outTextService;
  private final CountryService countryService;
  private final LanguageService languageService;
  @Override
  public void run(String... args) {
    LanguageConfig.setup(languageService);
    OutTextConfig.setup(outTextService);
    CountryConfig.setup(countryService, languageService);
  }
}
