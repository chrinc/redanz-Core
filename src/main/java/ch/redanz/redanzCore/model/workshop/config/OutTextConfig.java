package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.entities.OutTextId;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OutTextConfig {

  // Workflow
  LABEL_WORKFLOW_OPEN_EN("FRONT_LOGIN", "LABEL-WORKFLOW-OPEN", "Open", "EN"),
  LABEL_WORKFLOW_OPEN_GE("FRONT_LOGIN", "LABEL-WORKFLOW-OPEN", "Neu", "GE"),
  LABEL_WORKFLOW_SUBMITTED_EN("FRONT_LOGIN", "LABEL-WORKFLOW-SUBMITTED", "Submitted", "EN"),
  LABEL_WORKFLOW_SUBMITTED_GE("FRONT_LOGIN", "LABEL-WORKFLOW-SUBMITTED", "Eingereicht", "GE"),
  LABEL_WORKFLOW_CONFIRMING_EN("FRONT_LOGIN", "LABEL-WORKFLOW-CONFIRMING", "Confirming/Payment", "EN"),
  LABEL_WORKFLOW_CONFIRMING_GE("FRONT_LOGIN", "LABEL-WORKFLOW-CONFIRMING", "Bestätigung/Bezahlung", "GE"),
  LABEL_WORKFLOW_DONE_EN("FRONT_LOGIN", "LABEL-WORKFLOW-DONE", "Done", "EN"),
  LABEL_WORKFLOW_DONE_GE("FRONT_LOGIN", "LABEL-WORKFLOW-DONE", "Abgeschlossen", "GE"),
  LABEL_WORKFLOW_CANCELLED_EN("FRONT_LOGIN", "LABEL-WORKFLOW-CANCELLED", "Cancelled", "EN"),
  LABEL_WORKFLOW_CANCELLED_GE("FRONT_LOGIN", "LABEL-WORKFLOW-CANCELLED", "Annulliert", "GE"),

  // SLOTS
  LABEL_SLOT_THURSDAY_EN("FRONT_LOGIN", "LABEL-SLOT-THURSDAY", "Thursday", "EN"),
  LABEL_SLOT_THURSDAY_GE("FRONT_LOGIN", "LABEL-SLOT-THURSDAY", "Donnerstag", "GE"),
  LABEL_SLOT_FRIDAY_MORNING_EN("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-MORNING", "Friday Morning", "EN"),
  LABEL_SLOT_FRIDAY_MORNING_GE("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-MORNING", "Freitag Vormittag", "GE"),
  LABEL_SLOT_FRIDAY_AFTERNOON_EN("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-AFTERNOON", "Friday Afternoon", "EN"),
  LABEL_SLOT_FRIDAY_AFTERNOON_GE("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-AFTERNOON", "Freitag Nachmittag", "GE"),
  LABEL_SLOT_FRIDAY_EVENING_EN("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-EVENING", "Freitag Evening", "EN"),
  LABEL_SLOT_FRIDAY_EVENING_GE("FRONT_LOGIN", "LABEL-SLOT-FRIDAY-EVENING", "Freitag Abend", "GE"),
  LABEL_SLOT_FRIDAY_EN("FRONT_LOGIN", "LABEL-SLOT-FRIDAY", "Friday", "EN"),
  LABEL_SLOT_FRIDAY_GE("FRONT_LOGIN", "LABEL-SLOT-FRIDAY", "Freitag", "GE"),
  LABEL_SLOT_SATURDAY_EN("FRONT_LOGIN", "LABEL-SLOT-SATURDAY", "Saturday", "EN"),
  LABEL_SLOT_SATURDAY_GE("FRONT_LOGIN", "LABEL-SLOT-SATURDAY", "Samstag", "GE"),
  LABEL_SLOT_SATURDAY_LUNCH_EN("FRONT_LOGIN", "LABEL-SLOT-SATURDAY-LUNCH", "Saturday Lunch", "EN"),
  LABEL_SLOT_SATURDAY_LUNCH_GE("FRONT_LOGIN", "LABEL-SLOT-SATURDAY-LUNCH", "Samstag Mittag", "GE"),
  LABEL_SLOT_SUNDAY_EN("FRONT_LOGIN", "LABEL-SLOT-SUNDAY", "Sunday", "EN"),
  LABEL_SLOT_SUNDAY_GE("FRONT_LOGIN", "LABEL-SLOT-SUNDAY", "Sonntag", "GE"),
  LABEL_SLOT_SUNDAY_EVENING_EN("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-EVENING", "Sunday Evening", "EN"),
  LABEL_SLOT_SUNDAY_EVENING_GE("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-EVENING", "Sonntag Abend", "GE"),
  LABEL_SLOT_SUNDAY_EVENING_AFTER_PARTY_EN("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-EVENING-AFTER-PARTY", "Sunday Evening after the party", "EN"),
  LABEL_SLOT_SUNDAY_EVENING_AFTER_PARTY_GE("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-EVENING-AFTER-PARTY", "Sonntag Abend nach der Party", "GE"),
  LABEL_SLOT_SUNDAY_NIGHT_EN("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-NIGHT", "Sunday Night (to Monday)", "EN"),
  LABEL_SLOT_SUNDAY_NIGHT_GE("FRONT_LOGIN", "LABEL-SLOT-SUNDAY-NIGHT", "Sonntag Nacht (auf Montag)", "GE"),

  // Food
  LABEL_FOOD_VEGGIE_ASIAN_DESC_EN("FRONT_LOGIN", "LABEL-FOOD-VEGGIE-ASIAN-DESC", "Sandwich, Früchte, Getränk & Snack", "EN"),
  LABEL_FOOD_VEGGIE_ASIAN_DESC_GE("FRONT_LOGIN", "LABEL-FOOD-VEGGIE-ASIAN-DESC", "Sandwich, Fruits, Drinks & Snack", "GE"),
  LABEL_FOOD_SOUP_DESC_EN("FRONT_LOGIN", "LABEL-FOOD-SOUP-DESC", "Soupe and extras", "EN"),
  LABEL_FOOD_SOUP_DESC_GE("FRONT_LOGIN", "LABEL-FOOD-SOUP-DESC", "Suppe und Extras", "GE"),
  LABEL_FOOD_VEDA_DESC_EN("FRONT_LOGIN", "LABEL-FOOD-VEDA-DESC", "Healthy, organic, vegan", "EN"),
  LABEL_FOOD_VEDA_DESC_GE("FRONT_LOGIN", "LABEL-FOOD-VEDA-DESC", "Gesund, biologisch, vegan", "GE"),
  LABEL_FOOD_VEDA_NAME_EN("FRONT_LOGIN", "LABEL-FOOD-VEDA-NAME", "Healthy, organic, vegan", "EN"),
  LABEL_FOOD_VEDA_NAME_GE("FRONT_LOGIN", "LABEL-FOOD-VEDA-NAME", "Gesund, biologisch, vegan", "GE"),

  // Bundles
  LABEL_FULLPASS_DESC_EN("FRONT_LOGIN", "LABEL-FULLPASS-DESC", "4 FUN classes, 3 additional free choice classes, 3 Parties", "EN"),
  LABEL_FULLPASS_DESC_GE("FRONT_LOGIN", "LABEL-FULLPASS-DESC", "4 FUN-Klassen, 3 zusätzliche Klassen (freie Wahl), 3 Parties", "GE"),
  LABEL_PARTYPASS_DESC_EN("FRONT_LOGIN", "LABEL-PARTYPASS-DESC", "3 Parties", "EN"),
  LABEL_PARTYPASS_DESC_GE("FRONT_LOGIN", "LABEL-PARTYPASS-DESC", "3 Parties", "GE"),
  LABEL_HALFPASS_DESC_EN("FRONT_LOGIN", "LABEL-HALFPASS-DESC", "4 FUN classes, 3 Parties", "EN"),
  LABEL_HALFPASS_DESC_GE("FRONT_LOGIN", "LABEL-HALFPASS-DESC", "4 FUN-Klassen, 3 Parties", "GE"),
  LABEL_LEVELPASS_DESC_EN("FRONT_LOGIN", "LABEL-LEVELPASS-DESC", "4 FUN classes, 3 level classes, 3 Parties", "EN"),
  LABEL_LEVELPASS_DESC_GE("FRONT_LOGIN", "LABEL-LEVELPASS-DESC", "4 FUN-Klassen, 3 Level-Klassen, 3 Parties", "GE"),

  // Sleep Utils
  LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_EN("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MATTRESS-DOUBLE", "Double Mattress", "EN"),
  LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_GE("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MATTRESS-DOUBLE", "Doppelmatraze", "GE"),
  LABEL_SLEEP_UTIL_MATTRESS_SINGLE_EN("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MATTRESS-SINGLE", "Single Mattress", "EN"),
  LABEL_SLEEP_UTIL_MATTRESS_SINGLE_GE("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MATTRESS-SINGLE", "Einzelmatraze", "GE"),
  LABEL_SLEEP_UTIL_MAT_EN("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MAT", "Mat", "EN"),
  LABEL_SLEEP_UTIL_MAT_GE("FRONT_LOGIN", "LABEL-SLEEP-UTIL-MAT", "Matte", "GE"),
  LABEL_SLEEP_UTIL_COUCH_EN("FRONT_LOGIN", "LABEL-SLEEP-UTIL-COUCH", "Couch", "EN"),
  LABEL_SLEEP_UTIL_COUCH_GE("FRONT_LOGIN", "LABEL-SLEEP-UTIL-COUCH", "Sofa", "GE"),
  LABEL_SLEEP_UTIL_BAG_EN("FRONT_LOGIN", "LABEL-SLEEP-UTIL-BAG", "Sleeping Bag", "EN"),
  LABEL_SLEEP_UTIL_BAG_GE("FRONT_LOGIN", "LABEL-SLEEP-UTIL-BAG", "Schlafsack", "GE"),

  // Specials
  LABEL_SPECIAL_MASSAGE_NAME_EN("FRONT_LOGIN", "LABEL_SPECIAL_MASSAGE_NAME", "Massage", "EN"),
  LABEL_SPECIAL_MASSAGE_NAME_GE("FRONT_LOGIN", "LABEL_SPECIAL_MASSAGE_NAME", "Massage", "GE"),
  LABEL_SPECIAL_MASSAGE_DESC_EN("FRONT_LOGIN", "LABEL_SPECIAL_MASSAGE_DESC", "Massage Description", "EN"),
  LABEL_SPECIAL_MASSAGE_DESC_GE("FRONT_LOGIN", "LABEL_SPECIAL_MASSAGE_DESC", "Massage Beschreibung", "GE"),

  // VOLUNTEERING / DONATION / SCHOLARSHIP
  LABEL_DONATION_EN("FRONT_LOGIN", "LABEL_DONATION", "Donation", "EN"),
  LABEL_DONATION_GE("FRONT_LOGIN", "LABEL_DONATION", "Spende", "GE"),

  // discounts
  LABEL_DISCOUNT_EARLY_BIRD_EN("FRONT_LOGIN", "LABEL_DISCOUNT_EARLY_BIRD", "Early Bird", "EN"),
  LABEL_DISCOUNT_EARLY_BIRD_GE("FRONT_LOGIN", "LABEL_DISCOUNT_EARLY_BIRD", "Early Bird", "GE"),
  LABEL_DISCOUNT_STUDENT_EN("FRONT_LOGIN", "LABEL_DISCOUNT_STUDENT", "student", "EN"),
  LABEL_DISCOUNT_STUDENT_GE("FRONT_LOGIN", "LABEL_DISCOUNT_STUDENT", "Student", "GE"),
  LABEL_DISCOUNT_ABROAD_EN("FRONT_LOGIN", "LABEL_DISCOUNT_ABROAD", "Travel from abroad", "EN"),
  LABEL_DISCOUNT_ABROAD_GE("FRONT_LOGIN", "LABEL_DISCOUNT_ABROAD", "Anreise vom Ausland", "GE"),
  LABEL_DISCOUNT_EARLY_BIRD_DESC_EN("FRONT_LOGIN", "LABEL_DISCOUNT_EARLY_BIRD_DESC", "Early Bird", "EN"),
  LABEL_DISCOUNT_EARLY_BIRD_DESC_GE("FRONT_LOGIN", "LABEL_DISCOUNT_EARLY_BIRD_DESC", "Early Bird", "GE"),
  LABEL_DISCOUNT_STUDENT_DESC_EN("FRONT_LOGIN", "LABEL_DISCOUNT_STUDENT_DESC", "Are you student?", "EN"),
  LABEL_DISCOUNT_STUDENT_DESC_GE("FRONT_LOGIN", "LABEL_DISCOUNT_STUDENT_DESC", "Bist du Student?", "GE"),
  LABEL_DISCOUNT_ABROAD_DESC_EN("FRONT_LOGIN", "LABEL_DISCOUNT_ABROAD_DESC", "Are you travelling from abroad?", "EN"),
  LABEL_DISCOUNT_ABROAD_DESC_GE("FRONT_LOGIN", "LABEL_DISCOUNT_ABROAD_DESC", "Reist du vom Ausland an?", "GE"),

  // profile
  LABEL_EMAIL_CONFIRMED_EN("FRONT_BASE", "LABEL_EMAIL_CONFIRMED", "Thank you. Your email address has been confirmed.", "EN"),
  LABEL_EMAIL_CONFIRMED_GE("FRONT_BASE", "LABEL_EMAIL_CONFIRMED", "Vielen Dank, deine Email-Adresse wurde bestätigt.", "GE"),
  // EXCEPTION HANDLING
  LABEL_ERROR_USER_TAKEN_EN("FRONT_BASE", "LABEL-ERROR-USER-TAKEN", "Email already taken", "EN"),
  LABEL_ERROR_USER_TAKEN_GE("FRONT_BASE", "LABEL-ERROR-USER-TAKEN", "Die Email-Adresse wird bereits verwendet", "GE"),
  LABEL_ERROR_SUBMIT_EN("FRONT_BASE", "LABEL-ERROR-SUBMIT", "Submission Failed, please try again later", "EN"),
  LABEL_ERROR_SUBMIT_GE("FRONT_BASE", "LABEL-ERROR-SUBMIT", "Eingabe fehlgeschlagen, bitte versuche es später erneut", "GE"),
  LABEL_ERROR_UNEXPECTED_EN("FRONT_BASE", "LABEL-ERROR-UNEXPECTED", "There has been an error, try again later", "EN"),
  LABEL_ERROR_UNEXPECTED_GE("FRONT_BASE", "LABEL-ERROR-UNEXPECTED", "Es ist ein Fehler aufgetreten, bitte versuche es später erneut", "GE"),
  LABEL_ERROR_USER_NOT_FOUND_EN("FRONT_BASE", "LABEL-ERROR-USER-NOT-FOUND", "User not Found", "EN"),
  LABEL_ERROR_USER_NOT_FOUND_GE("FRONT_BASE", "LABEL-ERROR-USER-NOT-FOUND", "Benutzer nicht gefunden", "GE"),
  LABEL_ERROR_UNAUTHORIZED_EN("FRONT_BASE", "LABEL-ERROR-UNAUTHORIZED", "Unauthorized Attempt", "EN"),
  LABEL_ERROR_UNAUTHORIZED_GE("FRONT_BASE", "LABEL-ERROR-UNAUTHORIZED", "Fehlende Berechtigung", "GE"),
  LABEL_ERROR_AUTHENTICATION_EN("FRONT_BASE", "LABEL-ERROR-AUTHENTICATION", "Login failed", "EN"),
  LABEL_ERROR_AUTHENTICATION_GE("FRONT_BASE", "LABEL-ERROR-AUTHENTICATION", "Login fehlgeschlagen", "GE"),
  LABEL_ERROR_TIMEOUT_EN("FRONT_BASE", "LABEL-ERROR-TIMEOUT", "Timeout", "EN"),
  LABEL_ERROR_TIMEOUT_GE("FRONT_BASE", "LABEL-ERROR-TIMEOUT", "Zeit abgelaufen", "GE"),

  LABEL_ERROR_SAVE_DUPLICATE_EN("FRONT_BASE", "LABEL_ERROR_SAVE_DUPLICATE", "Duplicate Registration", "EN"),
  LABEL_ERROR_SAVE_DUPLICATE_GE("FRONT_BASE", "LABEL_ERROR_SAVE_DUPLICATE", "Anmeldung wurde bereits erfasst", "GE"),

  LABEL_ERROR_SAVE_REGISTRATION_EN("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Failed to save Registration", "EN"),
  LABEL_ERROR_SAVE_REGISTRATION_GE("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Fehler Speicherung Anmeldung", "GE"),

  LABEL_ERROR_SAVE_ACCOMMODATION_EN("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Failed to save Accommodation", "EN"),
  LABEL_ERROR_SAVE_ACCOMMODATION_GE("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Fehler Anmeldung Unterkunft", "GE"),

  LABEL_ERROR_SAVE_VOLUNTEERING_EN("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Failed to save Volunteering", "EN"),
  LABEL_ERROR_SAVE_VOLUNTEERING_GE("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Fehler Anmeldung Volunteering", "GE"),

  LABEL_ERROR_SAVE_DONATION_EN("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Failed to save Donation", "EN"),
  LABEL_ERROR_SAVE_DONATION_GE("FRONT_BASE", "LABEL_ERROR_SAVE_REGISTRATION", "Fehler Anmeldung Spenden", "GE"),
  LABEL_ERROR_SAVE_TRACK_EN("FRONT_BASE", "LABEL_ERROR_SAVE_TRACK", "Failed to save Track", "EN"),
  LABEL_ERROR_SAVE_TRACK_GE("FRONT_BASE", "LABEL_ERROR_SAVE_TRACK", "Fehler Anmeldung Track", "GE"),
  LABEL_ERROR_SAVE_WORKFLOW_EN("FRONT_BASE", "LABEL_ERROR_SAVE_WORKFLOW", "Failed to save Workflow", "EN"),
  LABEL_ERROR_SAVE_WORKFLOW_GE("FRONT_BASE", "LABEL_ERROR_SAVE_WORKFLOW", "Fehler Anmeldung Workflow", "GE"),
  LABEL_ERROR_TOKEN_EXPIRED_EN("FRONT_BASE", "LABEL_ERROR_TOKEN_EXPIRED", "Token Expired. Please please register again.", "EN"),
  LABEL_ERROR_TOKEN_EXPIRED_GE("FRONT_BASE", "LABEL_ERROR_TOKEN_EXPIRED", "Das Token ist abgelaufen. Bitte melde dich erneut an.", "GE"),
  LABEL_ERROR_EMAIL_CONFIRMED_EN("FRONT_BASE", "LABEL_ERROR_EMAIL_CONFIRMED", "Email already confirmed", "EN"),
  LABEL_ERROR_EMAIL_CONFIRMED_GE("FRONT_BASE", "LABEL_ERROR_EMAIL_CONFIRMED", "Email wurde bereits bestätigt", "GE"),

  // EMAIL

  // CONFIRM EMAIL
  LABEL_EMAIL_CONFIRM_EMAIL_BASE_EN("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_BASE", "Thank you for registering. Please click on the below link to activate your account.", "EN"),
  LABEL_EMAIL_CONFIRM_EMAIL_BASE_GE("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_BASE", "Vielen Dank für deine Anmeldung. Bitte klicke auf den Link unten, um deinen Account zu aktivieren.", "GE"),
  LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW_EN("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW", "Activate Now", "EN"),
  LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW_GE("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW", "Hier aktivieren", "GE"),
  LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES_EN("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES", "The Link will expire in 15 minutes.", "EN"),
  LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES_GE("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES", "Der Link verfällt in 15 Minuten", "GE"),
  LABEL_EMAIL_CONFIRM_SUBJECT_EN("EMAIL", "LABEL_EMAIL_CONFIRM_SUBJECT", "Email Confirmation", "EN"),
  LABEL_EMAIL_CONFIRM_SUBJECT_GE("EMAIL", "LABEL_EMAIL_CONFIRM_SUBJECT", "Email Bestätigung", "GE"),

  // CONFIRM EMAIL
  LABEL_EMAIL_RESET_PASSWORD_BASE_EN("EMAIL", "LABEL_EMAIL_RESET_PASSWORD_BASE", "Please click on the below link to reset your password.", "EN"),
  LABEL_EMAIL_RESET_PASSWORD_BASE_GE("EMAIL", "LABEL_EMAIL_RESET_PASSWORD_BASE", "Bitte klicke auf den Link unten, um deinen Passwort zurückzusetzen.", "GE"),
  LABEL_EMAIL_PASSWORD_RESET_NOW_EN("EMAIL", "LABEL_EMAIL_PASSWORD_RESET_NOW", "Reset Now", "EN"),
  LABEL_EMAIL_PASSWORD_RESET_NOW_GE("EMAIL", "LABEL_EMAIL_PASSWORD_RESET_NOW", "Hier zurücksetzen", "GE"),
  LABEL_EMAIL_RESET_PASSWORD_LINK_EXPIRES_EN("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES", "The Link will expire in 15 minutes.", "EN"),
  LABEL_EMAIL_RESET_PASSWORD_LINK_EXPIRES_GE("EMAIL", "LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES", "Der Link verfällt in 15 Minuten", "GE"),
  LABEL_EMAIL_RESET_PASSWORD_SUBJECT_EN("EMAIL", "LABEL_EMAIL_RESET_PASSWORD_SUBJECT", "Reset Password", "EN"),
  LABEL_EMAIL_RESET_PASSWORD_SUBJECT_GE("EMAIL", "LABEL_EMAIL_RESET_PASSWORD_SUBJECT", "Passwort zurücksetzen", "GE"),


  // SUBMITTED
  LABEL_EMAIL_SUBMITTED_HEADER_EN("EMAIL", "LABEL_EMAIL_SUBMITTED_HEADER", "Thank you for your registration!", "EN"),
  LABEL_EMAIL_SUBMITTED_HEADER_GE("EMAIL", "LABEL_EMAIL_SUBMITTED_HEADER", "Vielen Dank für deine Anmeldung!", "GE"),
  LABEL_EMAIL_SUBMITTED_BASE_EN("EMAIL", "LABEL_EMAIL_SUBMITTED_BASE", "We will check your entries and inform you as soon as possible if we can offer you a spot at Stir it! 2022.", "EN"),
  LABEL_EMAIL_SUBMITTED_BASE_GE("EMAIL", "LABEL_EMAIL_SUBMITTED_BASE", "Wir prüfen nun deine Eingaben und melden uns baldmöglichst bei dir mit der Info, ob wir dir einen Platz am Stir it! 2022 anbieten können.", "GE"),
  LABEL_EMAIL_SUBMITTED_DETAILS01_EN("EMAIL", "LABEL_EMAIL_SUBMITTED_DETAILS01", "You can find the details of your submission in your", "EN"),
  LABEL_EMAIL_SUBMITTED_DETAILS01_GE("EMAIL", "LABEL_EMAIL_SUBMITTED_DETAILS01", "Du findest die Angaben deiner Anmeldung in deinem", "GE"),
  LABEL_EMAIL_SUBMITTED_SUBJECT_EN("EMAIL", "LABEL_EMAIL_SUBMITTED_SUBJECT", "Registration submitted", "EN"),
  LABEL_EMAIL_SUBMITTED_SUBJECT_GE("EMAIL", "LABEL_EMAIL_SUBMITTED_SUBJECT", "Anmeldung eingereicht", "GE"),

  // RELEASED
  LABEL_EMAIL_RELEASED_HEADER01_EN("EMAIL", "LABEL_EMAIL_RELEASED_HEADER01", "Hurray - we've got a spot for you at Stir it! 2022", "EN"),
  LABEL_EMAIL_RELEASED_HEADER01_GE("EMAIL", "LABEL_EMAIL_RELEASED_HEADER01", "Hurra - wir haben einen Platz für dich beim Stir it! 2022", "GE"),
  LABEL_EMAIL_RELEASED_HEADER02_EN("EMAIL", "LABEL_EMAIL_RELEASED_HEADER02", "As soon as we receive your payment, we will confirm your participation", "EN"),
  LABEL_EMAIL_RELEASED_HEADER02_GE("EMAIL", "LABEL_EMAIL_RELEASED_HEADER02", "Sobald deine Zahlung bei uns eingegangen ist, können wir deine Teilnahme am Festival fix bestätigen", "GE"),
  LABEL_EMAIL_RELEASED_DETAILS_EN("EMAIL", "LABEL_EMAIL_RELEASED_DETAILS", "Please find the payment details in your ", "EN"),
  LABEL_EMAIL_RELEASED_DETAILS_GE("EMAIL", "LABEL_EMAIL_RELEASED_DETAILS", "Du findest die Zahlungsinformationen in deinem ", "GE"),
  LABEL_EMAIL_RELEASED_SUBJECT_EN("EMAIL", "LABEL_EMAIL_RELEASED_SUBJECT", "Registration confirmation", "EN"),
  LABEL_EMAIL_RELEASED_SUBJECT_GE("EMAIL", "LABEL_EMAIL_RELEASED_SUBJECT", "Anmeldebestätigung", "GE"),

  // DONE
  LABEL_EMAIL_DONE_HEADER_EN("EMAIL", "LABEL_EMAIL_DONE_HEADER", "We have received your payment and confirm your participation at Stirit 2022. We are looking forward to welcoming you in November!", "EN"),
  LABEL_EMAIL_DONE_HEADER_GE("EMAIL", "LABEL_EMAIL_DONE_HEADER", "Wir haben deine Zahlung erhalten und bestätigen deine Teilnahme am Stirit 2022. Wir freuen uns sehr, dich im November begrüssen zu dürfen!", "GE"),
  LABEL_EMAIL_DONE_BASE_EN("EMAIL", "LABEL_EMAIL_DONE_BASE", "We will provide you with more information regarding bands, party, work shop schedules, etc shortly before the festival.", "EN"),
  LABEL_EMAIL_DONE_BASE_GE("EMAIL", "LABEL_EMAIL_DONE_BASE", "Weitere Informationen zu Bands, Party, Zeiten für die Workshops, etc. werden wir die kurz vor dem Festival versenden.", "GE"),
  LABEL_EMAIL_DONE_SUBJECT_EN("EMAIL", "LABEL_EMAIL_DONE_SUBJECT", "Booking Confirmation", "EN"),
  LABEL_EMAIL_DONE_SUBJECT_GE("EMAIL", "LABEL_EMAIL_DONE_SUBJECT", "Buchungsbestätigung", "GE"),

  // REMINDER
  LABEL_EMAIL_REMINDER_HEADER_EN("EMAIL", "LABEL_EMAIL_REMINDER_HEADER", "You have registred for Stir it! 2022", "EN"),
  LABEL_EMAIL_REMINDER_HEADER_GE("EMAIL", "LABEL_EMAIL_REMINDER_HEADER", "Du hast dich für Stir it! 2022 angemeldet.", "GE"),
  LABEL_EMAIL_REMINDER_BASE01_EN("EMAIL", "LABEL_EMAIL_REMINDER_BASE01", "The payment needs to be completed within 14 days days after the registration confirmation. This time period has expired.", "EN"),
  LABEL_EMAIL_REMINDER_BASE01_GE("EMAIL", "LABEL_EMAIL_REMINDER_BASE01", "Die Zahlung muss innerhalb von 14 Tagen nach der Anmeldebestätigung erfolgt sein. Diese Frist ist abgelaufen.", "GE"),
  LABEL_EMAIL_REMINDER_BASE02_EN("EMAIL", "LABEL_EMAIL_REMINDER_BASE02", "We hope that you are still interested in being part of the workshop", "EN"),
  LABEL_EMAIL_REMINDER_BASE02_GE("EMAIL", "LABEL_EMAIL_REMINDER_BASE02", "Wir hoffen, dass du immernoch an der Teilnahme am Workshop interessiert bist.", "GE"),
  LABEL_EMAIL_REMINDER_BASE03_EN("EMAIL", "LABEL_EMAIL_REMINDER_BASE03", "We kindly ask you to make the payment within the next days, otherwise we will cancel your registration.", "EN"),
  LABEL_EMAIL_REMINDER_BASE03_GE("EMAIL", "LABEL_EMAIL_REMINDER_BASE03", "Wir bitten dich, die Zahlung in den nächsten Tagen auszulösen, sonst werden wir deine Anmeldung stornieren.", "GE"),
  LABEL_EMAIL_REMINDER_SUBJECT_EN("EMAIL", "LABEL_EMAIL_REMINDER_SUBJECT", "Payment reminder", "EN"),
  LABEL_EMAIL_REMINDER_SUBJECT_GE("EMAIL", "LABEL_EMAIL_REMINDER_SUBJECT", "Zahlungserinnerung", "GE"),

  // CANCEL
  LABEL_EMAIL_CANCEL_BASE01_EN("EMAIL", "LABEL_EMAIL_CANCEL_BASE01", "We haven't received your payment yet.", "EN"),
  LABEL_EMAIL_CANCEL_BASE01_GE("EMAIL", "LABEL_EMAIL_CANCEL_BASE01", "Wir haben bisher keine Zahlung von dir erhalten.", "GE"),
  LABEL_EMAIL_CANCEL_BASE02_EN("EMAIL", "LABEL_EMAIL_CANCEL_BASE02", "The payment needs to be completed within 14 days days after the registration confirmation. Since this time period has expired, your registration was cancelled. If you have made your payment recently or are about to pay, please contact us as soon as possible.", "EN"),
  LABEL_EMAIL_CANCEL_BASE02_GE("EMAIL", "LABEL_EMAIL_CANCEL_BASE02", "Die Zahlung muss innerhalb von 14 Tagen nach der Anmeldebestätigung erfolgt sein. Da diese Frist abgelaufen ist, wurde deine Buchung storniert. Bitte kontaktiere uns, falls du deine Zahlung kürzlich vorgenommen hast oder falls du gerade dabei bist.", "GE"),
  LABEL_EMAIL_CANCEL_SUBJECT_EN("EMAIL", "LABEL_EMAIL_CANCEL_SUBJECT", "Registration cancelled", "EN"),
  LABEL_EMAIL_CANCEL_SUBJECT_GE("EMAIL", "LABEL_EMAIL_CANCEL_SUBJECT", "Buchung storniert", "GE"),


  // GENERIC
  LABEL_EMAIL_ACCOUNT_EN("EMAIL", "LABEL_EMAIL_ACCOUNT", "Stir it! account", "EN"),
  LABEL_EMAIL_ACCOUNT_GE("EMAIL", "LABEL_EMAIL_ACCOUNT", "Stir it! account", "GE"),
  LABEL_EMAIL_SEE_YOU_EN("EMAIL", "LABEL_EMAIL_SEE_YOU", "See you soon", "EN"),
  LABEL_EMAIL_SEE_YOU_GE("EMAIL", "LABEL_EMAIL_SEE_YOU", "Bis bald", "GE"),
  LABEL_EMAIL_REGARDS_EN("EMAIL", "LABEL_EMAIL_REGARDS", "Best regards", "EN"),
  LABEL_EMAIL_REGARDS_GE("EMAIL", "LABEL_EMAIL_REGARDS", "Beste Grüsse", "GE"),
  LABEL_EMAIL_TEAM_EN("EMAIL", "LABEL_EMAIL_TEAM", "Your Stir it! Team", "EN"),
  LABEL_EMAIL_TEAM_GE("EMAIL", "LABEL_EMAIL_TEAM", "Dein Stir it! Team", "GE");

  private final String type;
  private final String outTextKey;
  private final String outText;
  private final String languageKey;

  public static void setup(OutTextService outTextService) {
    for (OutTextConfig outTextConfig : OutTextConfig.values()) {
      outTextService.save(
        new OutText(
          new OutTextId(
            outTextConfig.outTextKey
            , outTextConfig.languageKey
          ),
          outTextConfig.outText,
          outTextConfig.type
        )
      );
    }
  }
}
