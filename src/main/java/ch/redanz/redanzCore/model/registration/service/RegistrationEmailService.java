package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.repository.RegistrationEmailRepo;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventPartService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import ch.redanz.redanzCore.service.email.EmailService;
import com.google.gson.JsonObject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationEmailService {
  private final RegistrationEmailRepo registrationEmailRepo;
  private final OutTextService outTextService;
  private final LanguageService languageService;
  private final BaseParService baseParService;
  private final UserService userService;
  private final EmailService emailService;
  private final EventService eventService;
  private final EventPartService eventPartService;
  @Autowired
  Environment environment;

  @Autowired
  Configuration mailConfig;
  @Autowired
  private SpecialService specialService;

  public void update(RegistrationEmail registrationEmail) {
    registrationEmailRepo.save(registrationEmail);
  }

  public RegistrationEmail findByRegistration(Registration registration) {
    return registrationEmailRepo.findByRegistration(registration);
  }

  public void sendRegistrationSubmittedEmail(
    Registration registration
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationSubmitted.ftl");

    String languageKey =
      registration.getParticipant().getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        registration.getParticipant().getPersonLang().getLanguageKey();

    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_BASE_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", registration.getEvent().getName())
    );
    model.put("details",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SUBMITTED_DETAILS01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );

    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

//    log.info("incbfr send Email");
    emailService.sendEmail(
//      EmailService.getSession(),
      registration.getParticipant().getUser().getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,!registration.getEvent().isActive()
      ,null
    );
//    log.info("incbfr bfr update");
    update(
      new RegistrationEmail(registration, LocalDateTime.now())
    );
//    log.info("incbfr after update");
  }

  public void sendEmailBookingConfirmation(
    Person person,
    RegistrationEmail registrationEmail,
    Registration registration
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", person.getFirstName());
    Template template = mailConfig.getTemplate("registrationDone.ftl");

    String languageKey =
      person.getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        person.getPersonLang().getLanguageKey();
    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", eventService.findEventName(registration.getEvent()))
    );
    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_BASE_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("details",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_DETAILS01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );

    model.put("questions",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_QUESTIONS_ABOUT_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("cancellation",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCELLATION_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("refund",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REFUND_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("ticketTransfer",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TICKET_TRANSFER_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("waitingLists",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_WAITING_LISTS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("photoVideo",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_PHOTO_VIDEO_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("andMore",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_AND_MORE_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("checkOutTerms",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CHECK_TERMS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("checkOutTermsAt",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CHECK_TERMS_AT_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("termsUrl",
      outTextService.getOutTextByKeyAndLangKey(
        eventPartService.terms(registration.getEvent()),
        languageKey
      ).getOutText()
    );
    model.put("termsWebsite",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TERMS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("doneHappy",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_HAPPY_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );

    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );
    StringBuilder specialsHtml = new StringBuilder();

    // "specials"
    specialService.findByEventInfoOnly(registration.getEvent(), true).forEach(eventSpecial -> {
      String name = eventSpecial.getSpecial().getName();
      String description = eventSpecial.getSpecial().getDescription();
      String url = eventSpecial.getUrl();
    log.info(eventSpecial.getSpecial().getName());
      if (name != null && !name.isEmpty()) {
        specialsHtml.append("<div class=\"indented\">\uD83D\uDC49 <b>"
          + outTextService.getOutTextByKeyAndLangKey(name, languageKey).getOutText()
          + "</b>");
        if (description != null && !description.isEmpty()) {
          specialsHtml.append("<b>: </b>"
            + outTextService.getOutTextByKeyAndLangKey(description, languageKey).getOutText()
          );
        }
        if (url != null && !url.isEmpty()) {
          specialsHtml.append(" (<a href=\""
            + outTextService.getOutTextByKeyAndLangKey(url, languageKey).getOutText()
            + "\">"
            + outTextService.getOutTextByKeyAndLangKey("LABEL_LINK", languageKey).getOutText()
            + "</a>)");
        }
        specialsHtml.append("</div>");
      }
    });
    log.info(specialsHtml.toString());
    if (specialsHtml.length() > 0) {
      specialsHtml.insert(0,
        outTextService.getOutTextByKeyAndLangKey("LABEL_SPECIALS_INTRO", languageKey).getOutText()
          + "<br/><br/>");

    }
    model.put("specials", specialsHtml.toString());

    emailService.sendEmail(
      person.getUser().getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,!eventService.findIsActive(registration.getEvent())
      ,null
    );
    registrationEmail.setDoneSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendReminderEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());

    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationReminder.ftl");

    String languageKey =
      registration.getParticipant().getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        registration.getParticipant().getPersonLang().getLanguageKey();

    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", registration.getEvent().getName())
    );
    model.put("base01",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_BASE01_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.reminderAfterDays().toString())
    );
    model.put("base02",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_BASE02_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("base03",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_BASE03_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.cancelAfterDays().toString())
    );

    model.put("details",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_DETAILS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    emailService.sendEmail(
//      EmailService.getSession(),
      registration.getParticipant().getUser().getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,!registration.getEvent().isActive()
      ,null
    );

    registrationEmail.setReminderSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendCancellationEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationCancelled.ftl");

    String languageKey =
      registration.getParticipant().getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        registration.getParticipant().getPersonLang().getLanguageKey();

    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", registration.getEvent().getName())
    );
    model.put("base01",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCEL_BASE01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("base02",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCEL_BASE02_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.reminderAfterDays().toString())
    );
    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", registration.getEvent().getName())
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );
    emailService.sendEmail(
//      EmailService.getSession(),
      registration.getParticipant().getUser().getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCEL_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,!registration.getEvent().isActive()
      ,null
    );
    registrationEmail.setCancelledSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendEmailConfirmation(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());

    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationReleased.ftl");

    String languageKey =
      registration.getParticipant().getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        registration.getParticipant().getPersonLang().getLanguageKey();

    model.put("header01",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_HEADER01_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", eventService.findEventName(registration.getEvent()))
    );
    model.put("header02",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_HEADER02_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("details",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_DETAILS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("makePayment",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_MAKE_PAY_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.reminderAfterDays().toString())
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );

    model.put("see_you",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );

    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    emailService.sendEmail(
//      EmailService.getSession(),
      registration.getParticipant().getUser().getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,!eventService.findIsActive(registration.getEvent())
      ,null
    );

    registrationEmail.setReleasedSentDate(LocalDateTime.now());
    update(registrationEmail);

  }

  public void sendGenericEmail(Long senderUser, List<Registration> registrationList, JsonObject emailContent) throws IOException, TemplateException {
    String subject =
      emailContent.get("subject") != null ?
        (
          !emailContent.get("subject").isJsonNull() ?
          emailContent.get("subject").getAsString() : ""
        )
        : "";
    String content =
      emailContent.get("content") != null ?
        (
          !emailContent.get("content").isJsonNull() ?
          emailContent.get("content").getAsString() : ""
        )
        : "";

    registrationList.forEach(registration -> {
      try {
        sendGenericEmail(senderUser, registration.getParticipant(), subject, content);
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (TemplateException e) {
        throw new RuntimeException(e);
      }
    });
  }

  public void sendGenericEmail(Long senderUser, Person receiver, String subject, String content) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login"));

    // Name withough special chars, underscore instead of space
    model.put("firstName", receiver.getFirstName());
    model.put("content", content);
      Template template = null;
      try {
        template = mailConfig.getTemplate("generic.ftl");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      String languageKey =
        receiver.getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
          receiver.getPersonLang().getLanguageKey();

    model.put("regards",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

      try {
        emailService.sendEmail(
          receiver.getEmail(),
          subject,
          FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
          ,baseParService.testMailOnly()
          ,baseParService.testEmail()
          ,false
          ,userService.findByUserId(senderUser).getUsername()
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (TemplateException e) {
        throw new RuntimeException(e);
      }

  }

  public void postPoneLastReminderDate(Registration registration) {
    RegistrationEmail registrationEmail = registrationEmailRepo.findByRegistration(registration);

    if (registrationEmail.getReminderSentDate() != null) {
      registrationEmail.setReminderSentDate(LocalDateTime.now());
    }
  }

}
