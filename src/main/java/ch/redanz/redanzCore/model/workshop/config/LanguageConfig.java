package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum LanguageConfig {
  ENGLISH("EN", OutTextConfig.LABEL_LANGUAGE_ENGLISH_EN.getOutTextKey()),
  GERMAN("GE", OutTextConfig.LABEL_LANGUAGE_GERMAN_EN.getOutTextKey());

  private final String key;
  private final String name;

  public static void setup(LanguageService languageService) {
    for (LanguageConfig languageConfig : LanguageConfig.values()) {
      if (languageService.languageExists(languageConfig.key)) {
        Language language = languageService.findLanguageByLanguageKey(languageConfig.key);
        language.setName(languageConfig.name);
        languageService.save(language);
      } else {
        languageService.save(
          new Language(
            languageConfig.key,
            languageConfig.name
          )
        );
      }
    }
  }
}
