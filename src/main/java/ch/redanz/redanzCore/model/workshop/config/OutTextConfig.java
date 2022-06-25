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

    // Workflow
    LABEL_WORKFLOW_OPEN_EN("LABEL-WORKFLOW-OPEN", "Open", "EN"),
    LABEL_WORKFLOW_OPEN_GE("LABEL-WORKFLOW-OPEN", "Neu", "GE"),
    LABEL_WORKFLOW_SUBMITTED_EN("LABEL-WORKFLOW-SUBMITTED", "Submitted", "EN"),
    LABEL_WORKFLOW_SUBMITTED_GE("LABEL-WORKFLOW-SUBMITTED", "Eingereicht", "GE"),
    LABEL_WORKFLOW_CONFIRMING_EN("LABEL-WORKFLOW-CONFIRMING", "Confirming/Payment", "EN"),
    LABEL_WORKFLOW_CONFIRMING_GE("LABEL-WORKFLOW-CONFIRMING", "Bestätigung/Bezahlung", "GE"),
    LABEL_WORKFLOW_DONE_EN("LABEL-WORKFLOW-DONE", "Done", "EN"),
    LABEL_WORKFLOW_DONE_GE("LABEL-WORKFLOW-DONE", "Abgeschlossen", "GE"),
    LABEL_WORKFLOW_CANCELLED_EN("LABEL-WORKFLOW-CANCELLED", "Cancelled", "EN"),
    LABEL_WORKFLOW_CANCELLED_GE("LABEL-WORKFLOW-CANCELLED", "Annulliert", "GE"),

    // SLOTS
    LABEL_SLOT_THURSDAY_EN          ("LABEL-SLOT-THURSDAY"          , "Thursday", "EN"),
    LABEL_SLOT_THURSDAY_GE          ("LABEL-SLOT-THURSDAY"          , "Donnerstag", "GE"),
    LABEL_SLOT_FRIDAY_MORNING_EN    ("LABEL-SLOT-FRIDAY-MORNING"    , "Friday Morning", "EN"),
    LABEL_SLOT_FRIDAY_MORNING_GE    ("LABEL-SLOT-FRIDAY-MORNING"    , "Freitag Vormittag", "GE"),
    LABEL_SLOT_FRIDAY_AFTERNOON_EN  ("LABEL-SLOT-FRIDAY-AFTERNOON"  , "Friday Afternoon", "EN"),
    LABEL_SLOT_FRIDAY_AFTERNOON_GE  ("LABEL-SLOT-FRIDAY-AFTERNOON"  , "Freitag Nachmittag", "GE"),
    LABEL_SLOT_FRIDAY_EVENING_EN    ("LABEL-SLOT-FRIDAY-EVENING"    , "Freitag Evening", "EN"),
    LABEL_SLOT_FRIDAY_EVENING_GE    ("LABEL-SLOT-FRIDAY-EVENING"    , "Freitag Abend", "GE"),
    LABEL_SLOT_FRIDAY_EN            ("LABEL-SLOT-FRIDAY"            , "Friday", "EN"),
    LABEL_SLOT_FRIDAY_GE            ("LABEL-SLOT-FRIDAY"            , "Freitag", "GE"),
    LABEL_SLOT_SATURDAY_EN          ("LABEL-SLOT-SATURDAY"          , "Saturday", "EN"),
    LABEL_SLOT_SATURDAY_GE          ("LABEL-SLOT-SATURDAY"          , "Samstag", "GE"),
    LABEL_SLOT_SUNDAY_EN            ("LABEL-SLOT-SUNDAY"            , "Sunday", "EN"),
    LABEL_SLOT_SUNDAY_GE            ("LABEL-SLOT-SUNDAY"            , "Sonntag", "GE"),
    LABEL_SLOT_SUNDAY_EVENING_EN    ("LABEL-SLOT-SUNDAY-EVENING"    , "Sunday Evening", "EN"),
    LABEL_SLOT_SUNDAY_EVENING_GE    ("LABEL-SLOT-SUNDAY-EVENING"    , "Sonntag Abend", "GE"),
    LABEL_SLOT_SUNDAY_NIGHT_EN      ("LABEL-SLOT-SUNDAY-NIGHT"      , "Sunday Night (to Monday)", "EN"),
    LABEL_SLOT_SUNDAY_NIGHT_GE      ("LABEL-SLOT-SUNDAY-NIGHT"      , "Sonntag Nacht (auf Montag)", "GE"),

    // Food
    LABEL_FOOD_VEGGIE_ASIAN_DESC_EN("LABEL-FOOD-VEGGIE-ASIAN-DESC", "Sandwich, Früchte, Getränk & Snack", "EN"),
    LABEL_FOOD_VEGGIE_ASIAN_DESC_GE("LABEL-FOOD-VEGGIE-ASIAN-DESC", "Sandwich, Fruits, Drinks & Snack", "GE"),
    LABEL_FOOD_SOUP_DESC_EN("LABEL-FOOD-SOUP-DESC", "Soupe and extras", "EN"),
    LABEL_FOOD_SOUP_DESC_GE("LABEL-FOOD-SOUP-DESC", "Suppe und Extras", "GE"),

    // Bundles
    LABEL_FULLPASS_DESC_EN("LABEL-FULLPASS-DESC", "10 Classes on Friday, Saturday, Sunday & 3 Parties", "EN"),
    LABEL_FULLPASS_DESC_GE("LABEL-FULLPASS-DESC", "10 Klassen am Freitag, Samstag, Sonntag & 3 Parties", "GE"),
    LABEL_PARTYPASS_DESC_EN("LABEL-PARTYPASS-DESC", "3 Parties", "EN"),
    LABEL_PARTYPASS_DESC_GE("LABEL-PARTYPASS-DESC", "3 Parties", "GE"),
    LABEL_HALFPASS_DESC_EN("LABEL-HALFPASS-DESC", "10 Free-Choice afternoon classes on Friday, Saturday, Sunday & 3 Parties", "EN"),
    LABEL_HALFPASS_DESC_GE("LABEL-HALFPASS-DESC", "10 Free-Choice Nachmittags-Klassen am Freitag, Samstag, Sonntag & 3 Parties", "GE"),

    // Sleep Utils
    LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_EN("LABEL-SLEEP-UTIL-MATTRESS-DOUBLE", "Double Mattress", "EN"),
    LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_GE("LABEL-SLEEP-UTIL-MATTRESS-DOUBLE", "Doppelmatraze", "GE"),
    LABEL_SLEEP_UTIL_MATTRESS_SINGLE_EN("LABEL-SLEEP-UTIL-MATTRESS-SINGLE", "Single Mattress", "EN"),
    LABEL_SLEEP_UTIL_MATTRESS_SINGLE_GE("LABEL-SLEEP-UTIL-MATTRESS-SINGLE", "Einzelmatraze", "GE"),
    LABEL_SLEEP_UTIL_MAT_EN("LABEL-SLEEP-UTIL-MAT", "Mat", "EN"),
    LABEL_SLEEP_UTIL_MAT_GE("LABEL-SLEEP-UTIL-MAT", "Matte", "GE"),
    LABEL_SLEEP_UTIL_COUCH_EN("LABEL-SLEEP-UTIL-COUCH", "Couch", "EN"),
    LABEL_SLEEP_UTIL_COUCH_GE("LABEL-SLEEP-UTIL-COUCH", "Sofa", "GE"),
    LABEL_SLEEP_UTIL_BAG_EN("LABEL-SLEEP-UTIL-BAG", "Sleeping Bag", "EN"),
    LABEL_SLEEP_UTIL_BAG_GE("LABEL-SLEEP-UTIL-BAG", "Schlafsack", "GE"),

    // EXCEPTION HANDLING
    LABEL_ERROR_USER_TAKEN_EN("LABEL-ERROR-USER-TAKEN", "Email already taken", "EN"),
    LABEL_ERROR_USER_TAKEN_GE("LABEL-ERROR-USER-TAKEN", "Die Email-Adresse wird bereits verwendet", "GE"),
    LABEL_ERROR_SUBMIT_EN("LABEL-ERROR-SUBMIT", "Submission Failed, please try again later", "EN"),
    LABEL_ERROR_SUBMIT_GE("LABEL-ERROR-SUBMIT", "Eingabe fehlgeschlagen, bitte versuche es später erneut", "GE"),
    LABEL_ERROR_UNEXPECTED_EN("LABEL-ERROR-UNEXPECTED", "There has been an error, try again later", "EN"),
    LABEL_ERROR_UNEXPECTED_GE("LABEL-ERROR-UNEXPECTED", "Es ist ein Fehler aufgetreten, bitte versuche es später erneut", "GE"),
    LABEL_ERROR_UNAUTHORIZED_EN("LABEL-ERROR-UNAUTHORIZED", "Unauthorized Attempt", "EN"),
    LABEL_ERROR_UNAUTHORIZED_GE("LABEL-ERROR-UNAUTHORIZED", "Fehlende Berechtigung", "GE");

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
