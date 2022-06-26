package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.profile.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepo extends JpaRepository<Language, String> {
    Language findLanguageByLanguageKey(String key);
}
