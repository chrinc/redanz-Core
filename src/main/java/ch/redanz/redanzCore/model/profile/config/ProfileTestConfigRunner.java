package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Order(700)
@Profile("dev || test")
//@Profile("test")
public class ProfileTestConfigRunner implements CommandLineRunner {
  private final UserService userService;
  private final CountryService countryService;
  private final PersonService personService;
  private final LanguageService languageService;

  @Override
  public void run(String... args) {
    UserConfig.setup(userService);
    PersonConfig.setup(personService, userService, countryService, languageService);
  }
}
