package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LanguageConfig {
  ENGLISH("EN", "English"),
  GERMAN("GE", "German");

  private final String key;
  private final String name;

  public static void setup(LanguageService languageService) {
    for (LanguageConfig languageConfig : LanguageConfig.values()) {
      if (!languageService.languageExists(languageConfig.key)) {
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
