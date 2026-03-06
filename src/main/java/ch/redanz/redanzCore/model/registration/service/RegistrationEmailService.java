package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.repository.RegistrationEmailRepo;
import ch.redanz.redanzCore.model.registration.response.PaymentDetailsResponse;
import ch.redanz.redanzCore.model.workshop.config.EventPartConfig;
import ch.redanz.redanzCore.model.workshop.configTest.EventPartInfoConfig;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.*;
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
import java.time.OffsetDateTime;
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
  @Autowired
  private PersonService personService;

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
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", registration.getParticipant().getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));


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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    emailService.sendEmail(
      registration.getParticipant().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      , userService.emailIsTester(registration.getParticipant().getEmail())
      , !registration.getEvent().isActive()
      , null
    );
    update(
      new RegistrationEmail(registration, OffsetDateTime.now().toZonedDateTime())
    );
  }

  public void sendEmailBookingConfirmation(
    Person person,
    RegistrationEmail registrationEmail,
    Registration registration,
    PaymentDetailsResponse paymentDetailsResponse
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", person.getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

    Template template = mailConfig.getTemplate("registrationDone.ftl");

    String languageKey =
      person.getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        person.getPersonLang().getLanguageKey();
    model.put("header",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_HEADER_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    if (baseParService.bookletLink(registration.getEvent(), languageKey).isEmpty()) {
      model.put("base",
        outTextService.getOutTextByKeyAndLangKey(
          OutTextConfig.LABEL_EMAIL_DONE_BASE_EN.getOutTextKey(),
          languageKey
        ).getOutText()
      );
    } else {
      model.put("base",
        outTextService.getOutTextByKeyAndLangKey(
          OutTextConfig.LABEL_EMAIL_DONE_BASE_BOOKLET_READY_EN.getOutTextKey(),
          languageKey
        ).getOutText().replace(
        "{1}"
          , "<a href=\"" + baseParService.bookletLink(registration.getEvent(), languageKey)
            + "\">" + outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_BOOKLET_EN.getOutTextKey(), languageKey).getOutText()
            + "</a>"
        )
      );
    }
    model.put("details",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_DETAILS01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("registrationList",
      getEmailOverview(registration, paymentDetailsResponse, languageKey)
    );

    model.put("details2",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_DETAILS02_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
            + outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_LINK_EN.getOutTextKey(), languageKey).getOutText()
            + "</a>)"
            + "<br/><br/>");
        }
        specialsHtml.append("</div>");
      }
    });
    if (specialsHtml.length() > 0) {
      specialsHtml.insert(0,
        outTextService.getOutTextByKeyAndLangKey("LABEL_SPECIALS_INTRO", languageKey).getOutText()
          + "<br/><br/>");

    }
    model.put("specials", specialsHtml.toString());

    emailService.sendEmail(
      person.getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      , userService.emailIsTester(person.getEmail())
      , !eventService.findIsActive(registration.getEvent())
      , null
    );
    registrationEmail.setDoneSentDate(OffsetDateTime.now().toZonedDateTime());
    update(registrationEmail);
  }

  public void sendReminderEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", registration.getParticipant().getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

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
      ).getOutText().replace("{1}", baseParService.reminderAfterDays(registration.getEvent()).toString())
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
      ).getOutText().replace("{1}", baseParService.cancelAfterDays(registration.getEvent()).toString())
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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
    );
    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    emailService.sendEmail(
      registration.getParticipant().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      , userService.emailIsTester(registration.getParticipant().getEmail())
      , !registration.getEvent().isActive()
      , null
    );

    registrationEmail.setReminderSentDate(OffsetDateTime.now().toZonedDateTime());
    update(registrationEmail);
  }

  public void sendCancellationEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("firstName", registration.getParticipant().getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

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
      ).getOutText().replace("{1}", baseParService.reminderAfterDays(registration.getEvent()).toString())
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
      registration.getParticipant().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCEL_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      , userService.emailIsTester(registration.getParticipant().getEmail())
      , !registration.getEvent().isActive()
      , null
    );
    registrationEmail.setCancelledSentDate(OffsetDateTime.now().toZonedDateTime());
    update(registrationEmail);
  }

  public void sendEmailConfirmation(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + registration.getParticipant().getPersonLang().getLanguageKey().toLowerCase());
    model.put("firstName", registration.getParticipant().getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

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
      ).getOutText().replace("{1}", baseParService.reminderAfterDays(registration.getEvent()).toString())
    );
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
      registration.getParticipant().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      , userService.emailIsTester(registration.getParticipant().getEmail())
      , !eventService.findIsActive(registration.getEvent())
      , null
    );

    registrationEmail.setReleasedSentDate(OffsetDateTime.now().toZonedDateTime());
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
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    // Name withough special chars, underscore instead of space
    model.put("firstName", receiver.getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

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
      ).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
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
        , userService.emailIsTester(receiver.getEmail())
        , false
        , personService.findByUser(userService.findByUserId(senderUser)).getEmail()
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
      registrationEmail.setReminderSentDate(OffsetDateTime.now().toZonedDateTime());
    }
  }

  public String getEmailOverview(Registration registration, PaymentDetailsResponse paymentDetailsResponse, String languageKey) {
    StringBuilder sb = new StringBuilder(2048);

    sb.append("""
        <div style="font-family:system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;max-width:640px;">
          <div style="border:1px solid #e6e6e6;border-radius:16px;padding:20px;">
    """);

    sb.append("<div style=\"font-size:20px;font-weight:700;margin:0 0 4px 0;\">")
      .append(escapeHtml(outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_YOUR_BOOKING_EN.getOutTextKey(), languageKey).getOutText()))
      .append("</div>");

    // optional small line showing paid/due breakdown
    sb.append("<div style=\"color:#666;font-size:14px;margin:0 0 8px 0;\">")
      .append(outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_PAID_EN.getOutTextKey(), languageKey).getOutText() + ": ").append(escapeHtml(chf(paymentDetailsResponse.getAmountPaid())))
      .append(" &nbsp;|&nbsp; ")
      .append(outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_DUE_EN.getOutTextKey(), languageKey).getOutText() +": ").append(escapeHtml(chf(paymentDetailsResponse.getAmountDue())))
      .append("</div>");

    // Sections (only render if present)

    renderSection(sb, "", safePairs(paymentDetailsResponse.getItems()), false, languageKey, false, true, false);
    renderSection(sb, eventPartService.eventPartTitleExist(registration.getEvent(), EventPartConfig.SPECIAL.getEventPartKey(), languageKey), safePairs(paymentDetailsResponse.getSpecials()), false, languageKey, true, false, false);
    renderSection(sb, eventPartService.eventPartTitleExist(registration.getEvent(), EventPartConfig.PRIVATE.getEventPartKey(), languageKey), safePairs(paymentDetailsResponse.getPrivateClasses()), false, languageKey, true, false, false);
    renderSection(sb, eventPartService.eventPartTitleExist(registration.getEvent(), EventPartConfig.SOLIDARITY_FUND.getEventPartKey(), languageKey), safePairs(paymentDetailsResponse.getDonation()), false, languageKey,true, false, false);
    renderSection(sb, eventPartService.eventPartTitleExist(registration.getEvent(), EventPartConfig.FOOD.getEventPartKey(), languageKey), safePairs(paymentDetailsResponse.getFoodSlots()), false, languageKey, true, false, false);
    renderSection(sb, eventPartService.eventPartTitleExist(registration.getEvent(), EventPartConfig.DISCOUNT.getEventPartKey(), languageKey), safePairs(paymentDetailsResponse.getDiscounts()), true, languageKey, true, false, true);

    // Divider
    sb.append("<div style=\"border-top:1px solid #222;margin:8px 0;\"></div>");

    // Total
    sb.append("<div style=\"display:flex;justify-content:space-between;align-items:flex-end;\">")
      .append("<div style=\"font-size:16px;font-weight:700;\">Total</div>")
      .append("<div style=\"font-size:16px;font-weight:700;\">")
      .append(escapeHtml(chf(paymentDetailsResponse.getTotalAmount())))
      .append("</div></div>");

    sb.append("""
          </div>
        </div>
    """);

    return sb.toString();
  }

  /* ---------------- Rendering helpers ---------------- */

  /**
   * Renders a section with rows like "Label" .... "CHF 123.-"
   * If asDiscounts=true, amounts are shown as "- CHF x.-"
   */
  private void renderSection(StringBuilder sb, String title, List<Pair> items, boolean asDiscounts, String languageKey, boolean hasTitle, boolean bold, boolean italic) {
    if (items == null || items.isEmpty()) return;
    if (hasTitle) {
      sb.append("<div style=\"margin:14px 0 0 0;font-size:15px;font-weight:700;color:#111;\">")
        .append(escapeHtml(title))
        .append("</div>");
    }

    for (Pair it : items) {
      if (it == null || isBlank(it.label) || it.amount == null) continue;

      String humanizedLabel = asDiscounts ?
        "- " + humanizeLabel(it.label, languageKey)
        : humanizeLabel(it.label, languageKey);

      sb.append("<div style=\"display:flex;justify-content:space-between;gap:16px;padding:3px 0;\">")
        .append("<div style=\"color:#222"
          + (bold ? ";font-weight:700" : "")
          + (italic ? ";font-style:italic" : "")
          + "\">")
        .append(escapeHtml(humanizedLabel))
        .append("</div>")
        .append("<div style=\"white-space:nowrap;color:#222"
          + (italic ? ";font-style:italic" : "")
          +"\">")
        .append(escapeHtml(chf(it.amount)))
        .append("</div>")
        .append("</div>");
    }

  }

/* ---------------- Data shape helpers ----------------
   Your response shows list-of-lists like [[Half Pass, 250]]
   So we convert Object pairs into Pair(label, amount).
*/

  private List<Pair> safePairs(List<List<String>> raw) {
    if (raw == null || raw.isEmpty()) return Collections.emptyList();

    return raw.stream()
      .map(this::toPair)
      .filter(p -> p != null && !isBlank(p.label) && p.amount != null)
      .toList();
  }

  private Pair toPair(List<String> row) {
    if (row == null || row.size() < 2) return null;

    String label = row.get(0) == null ? null : row.get(0).toString();

    Long amount = null;
    Object a = row.get(1);
    if (a instanceof Long bd) amount = bd;
    else if (a instanceof Number n) amount = Long.valueOf((long) n.doubleValue());
    else if (a != null) {
      try {
        amount = Long.parseLong((String) a);
      } catch (Exception ignore) { /* leave null */ }
    }

    return new Pair(label, amount);
  }

  private static Long coalesce(Long v) {
    return v == null ? 0 : v;
  }

  /* ---------------- Formatting helpers ---------------- */

  private static String chf(Long amount) {
    if (amount == null) return "CHF 0.-";
    return "CHF " + amount + ".-";
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  private static String escapeHtml(String s) {
    if (s == null) return "";
    return s.replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#39;");
  }

  /**
   * Optional: map internal labels to nice display strings.
   * Keep as-is if you don't need it.
   */
  private String humanizeLabel(String label, String langKey) {
    if (label == null) return "";

    return outTextService.outTextExists(label, langKey) ? outTextService.getOutTextByKeyAndLangKey(label, langKey).getOutText() : label;
  }

  /* ---------------- Minimal internal DTO for rendering ---------------- */

  private class Pair {
    final String label;
    final Long amount;
    Pair(String label, Long amount) {
      this.label = label;
      this.amount = amount;
    }
  }
}

