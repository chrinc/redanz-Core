package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.profile.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum LanguageConfig {
    ENGLISH( "EN", "English"),
    GERMAN( "GE","German");

    private final String key;
    private final String name;

    public static List<Language> setup() {
        List<Language> transitions = new ArrayList<>();
        for (LanguageConfig languageConfig : LanguageConfig.values()) {
            transitions.add(
                    new Language(
                            languageConfig.getKey(),
                            languageConfig.getName()
                    )
            );
        }
        return transitions;
    }
}
