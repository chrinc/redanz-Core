package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
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
public class ProfileTestConfigRunner implements CommandLineRunner {
  private final UserRepo userRepo;
  private final PersonRepo personRepo;
  private final CountryService countryService;
  private final LanguageRepo languageRepo;

  @Override
  public void run(String... args) throws Exception {
    userRepo.saveAll(UserConfig.setup());
    personRepo.saveAll(PersonConfig.setup(userRepo, countryService, languageRepo));
  }
}
