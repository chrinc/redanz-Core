package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LanguageService {

  private final LanguageRepo languageRepo;

  public void save(Language language) {
    languageRepo.save(language);
  }
  public Language findLanguageByLanguageKey(String key) {
    return languageRepo.findLanguageByLanguageKey(key);
  }

  ;
}
