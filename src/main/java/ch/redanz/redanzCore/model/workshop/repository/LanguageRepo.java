package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.profile.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepo extends JpaRepository<Language, String> {
  Optional<Language> findLanguageByLanguageKey(String key);
}
