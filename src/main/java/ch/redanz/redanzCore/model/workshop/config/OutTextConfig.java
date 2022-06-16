package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.Bundle;
import ch.redanz.redanzCore.model.workshop.OutText;
import ch.redanz.redanzCore.model.workshop.OutTextId;
import ch.redanz.redanzCore.model.workshop.Track;
//import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import ch.redanz.redanzCore.model.workshop.repository.LanguageRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum OutTextConfig {
    LABEL_FULLPASS_DESC_EN("LABEL-FULLPASS-DESC", "10 Classes on Friday, Saturday, Sunday & 3 Parties", "EN"),
    LABEL_FULLPASS_DESC_GE("LABEL-FULLPASS-DESC", "10 Klassen am Freitag, Samstag, Sonntag & 3 Parties", "GE"),
    LABEL_PARTYPASS_DESC_EN("LABEL-PARTYPASS-DESC", "3 Parties", "EN"),
    LABEL_PARTYPASS_DESC_GE("LABEL-PARTYPASS-DESC", "3 Parties", "GE"),
    LABEL_HALFPASS_DESC_EN("LABEL-HALFPASS-DESC", "10 Free-Choice afternoon classes on Friday, Saturday, Sunday & 3 Parties", "EN"),
    LABEL_HALFPASS_DESC_GE("LABEL-HALFPASS-DESC", "10 Free-Choice Nachmittags-Klassen am Freitag, Samstag, Sonntag & 3 Parties", "GE");

    private final String outTextKey;
    private final String outText;
    private final String languageKey;

    public static List<OutText> setup(LanguageRepo languageRepo) {
        List<OutText> transitions = new ArrayList<>();
        for (OutTextConfig outTextConfig : OutTextConfig.values()) {
           transitions.add(
             new OutText(
               new OutTextId(
                       outTextConfig.outTextKey
                       ,outTextConfig.languageKey
               ),
               outTextConfig.outText
             )
           );
        }
        return transitions;
    }
}
