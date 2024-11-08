package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.workshop.configTest.LanguageConfig;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class LanguageService {

  private final LanguageRepo languageRepo;

  public void save(Language language) {
    languageRepo.save(language);
  }
  public List<Language> findAll() {return languageRepo.findAll(); }
  public Language findLanguageByLanguageKey(String key) {
    return languageRepo.findLanguageByLanguageKey(key).get();
  }
  public boolean languageExists(String key) {
    return languageRepo.findLanguageByLanguageKey(key).isPresent();
  }
  public Language english() {
    return findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey());
  }
}
