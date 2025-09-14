package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.configTest.LanguageConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;

@Slf4j
@Getter
@AllArgsConstructor
public enum PersonConfig {
  FRANKY(UserConfig.FRANKY_USER, "Franky", "Manning", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.FRANKY_USER.getUsername()),
  CLAUDIA(UserConfig.CLAUDIA_USER, "Claudia", "Fonte", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.CLAUDIA_USER.getUsername()),
  NORMA(UserConfig.NORMA_USER, "Norma", "Miller", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.NORMA_USER.getUsername()),
  EDDIE(UserConfig.EDDIE_USER, "Eddie", "Davis", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.EDDIE_USER.getUsername()),
  WILLIAM(UserConfig.WILLIAM_USER, "William", "Downes", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.WILLIAM_USER.getUsername()),
  ELNORA(UserConfig.ELNORA_USER, "Elnora", "Dyson", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.ELNORA_USER.getUsername()),
  ARLYNE(UserConfig.ARLYNE_USER, "Arlyne", "Evans", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.ARLYNE_USER.getUsername()),
  BILLY(UserConfig.BILLY_USER, "Billy", "Ricker", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.BILLY_USER.getUsername()),
  NAOMI(UserConfig.NAOMI_USER, "Naomi", "Waller", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.NAOMI_USER.getUsername()),
  ESTHER(UserConfig.ESTHER_USER, "Esther", "Washington", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.ESTHER_USER.getUsername()),
  ANN(UserConfig.ANN_USER, "Ann", "Johnson", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.ANN_USER.getUsername()),
  MILDRED(UserConfig.MILDRED_USER, "Mildred", "Pollard", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.MILDRED_USER.getUsername()),
  RUTHIE(UserConfig.RUTHIE_USER, "Ruthie", "Reingold", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.RUTHIE_USER.getUsername()),
  WILLA(UserConfig.WILLA_USER, "Willa", "Ricker", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.WILLA_USER.getUsername()),
  HARRY(UserConfig.HARRY_USER, "Harry", "Rosenberg", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.ENGLISH, UserConfig.HARRY_USER.getUsername()),
  OLIVER(UserConfig.OLIVER_USER, "Oliver", "Washington", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.OLIVER_USER.getUsername()),
  ORG_SONNY(UserConfig.ORG_SONNY_USER, "Sonny", "Jenkins", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.ORG_SONNY_USER.getUsername()),
  ORG_ANN(UserConfig.ORG_ANN_USER, "Ann", "Johnson", "Bahnhofstrasse 1", "8000", "Zürich", "ch", LanguageConfig.GERMAN, UserConfig.ORG_ANN_USER.getUsername());

  private final UserConfig userConfig;
  private final String firstName;
  private final String lastName;
  private final String street;
  private final String postalCode;
  private final String city;
  private final String countryKey;
  private final LanguageConfig languageConfig;
  private final String email;

  public static void setup(PersonService personService, UserService userService, CountryService countryService, LanguageService languageService) {
    for (PersonConfig personConfig : PersonConfig.values()) {

      personService.save(
        new Person(
          userService.getUser(personConfig.userConfig.getUsername()),
          personConfig.firstName,
          personConfig.lastName,
          personConfig.street,
          personConfig.postalCode,
          personConfig.city,
          countryService.findCountry(personConfig.countryKey),
          languageService.findLanguageByLanguageKey(personConfig.getLanguageConfig().getKey()),
          LocalDateTime.now(),
          personConfig.email,
          true
        )
      );
    }
  }
}

