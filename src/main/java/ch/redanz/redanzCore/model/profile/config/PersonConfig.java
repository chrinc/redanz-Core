package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.config.LanguageConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum PersonConfig {
  FRANKY(UserConfig.FRANKY_USER, "Franky", "Manning", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  CLAUDIA(UserConfig.CLAUDIA_USER, "Claudia", "Fonte", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  NORMA(UserConfig.NORMA_USER, "Norma", "Miller", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  EDDIE(UserConfig.EDDIE_USER, "Eddie", "Davis", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  WILLIAM(UserConfig.WILLIAM_USER, "William", "Downes", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  ELNORA(UserConfig.ELNORA_USER, "Elnora", "Dyson", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  ARLYNE(UserConfig.ARLYNE_USER, "Arlyne", "Evans", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  BILLY(UserConfig.BILLY_USER, "Billy", "Ricker", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  NAOMI(UserConfig.NAOMI_USER, "Naomi", "Waller", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  ESTHER(UserConfig.ESTHER_USER, "Esther", "Washington", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  ANN(UserConfig.ANN_USER, "Ann", "Johnson", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  MILDRED(UserConfig.MILDRED_USER, "Mildred", "Pollard", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  RUTHIE(UserConfig.RUTHIE_USER, "Ruthie", "Reingold", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  WILLA(UserConfig.WILLA_USER, "Willa", "Ricker", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  HARRY(UserConfig.HARRY_USER, "Harry", "Rosenberg", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.ENGLISH),
  OLIVER(UserConfig.OLIVER_USER, "Oliver", "Washington", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  ORG_SONNY(UserConfig.ORG_SONNY_USER, "Sonny", "Jenkins", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN),
  ORG_ANN(UserConfig.ORG_ANN_USER, "Ann", "Johnson", "Bahnhofstrasse 1", "8000", "Zürich", Long.valueOf(756), LanguageConfig.GERMAN);

  private final UserConfig userConfig;
  private final String firstName;
  private final String lastName;
  private final String street;
  private final String postalCode;
  private final String city;
  private final Long countryId;
  private final LanguageConfig languageConfig;

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
          // countries need to be loaded first
          null,
//          countryService.findCountry(personConfig.countryId),
          languageService.findLanguageByLanguageKey(personConfig.getLanguageConfig().getKey()),
          userService.getUser(personConfig.userConfig.getUsername()).getUsername(),
          true
        )
      );
    }
  }
}

