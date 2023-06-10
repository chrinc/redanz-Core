package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.repository.RegistrationEmailRepo;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
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
//  private final RegistrationService registrationService;
  @Autowired
  Environment environment;

  @Autowired
  Configuration mailConfig;

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
    model.put("link", environment.getProperty("link.login"));
    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationSubmitted.ftl");
    // log.info("send email?");
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
      ).getOutText()
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
      ).getOutText()
    );
    EmailService.sendEmail(
      EmailService.getSession(),
      registration.getParticipant().getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,!registration.getEvent().isActive()
    );
    update(
      new RegistrationEmail(registration, LocalDateTime.now())
    );
  }

  public void sendEmailBookingConfirmation(
    Person person,
    RegistrationEmail registrationEmail,
    Registration registration
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
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
      ).getOutText()
    );
    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_BASE_EN.getOutTextKey(),
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
      ).getOutText()
    );
    EmailService.sendEmail(
      EmailService.getSession(),
      person.getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_DONE_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,!registration.getEvent().isActive()
    );
    registrationEmail.setDoneSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendReminderEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("link", environment.getProperty("link.login"));
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
      ).getOutText()
    );
    model.put("base01",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_BASE01_EN.getOutTextKey(),
        languageKey
      ).getOutText()
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
      ).getOutText()
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
      ).getOutText()
    );
    EmailService.sendEmail(
      EmailService.getSession(),
      registration.getParticipant().getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_REMINDER_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,!registration.getEvent().isActive()
    );

    registrationEmail.setReminderSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendCancellationEmail(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("link", environment.getProperty("link.login"));
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
      ).getOutText()
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
      ).getOutText()
    );
    EmailService.sendEmail(
      EmailService.getSession(),
      registration.getParticipant().getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CANCEL_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,!registration.getEvent().isActive()
    );
    registrationEmail.setCancelledSentDate(LocalDateTime.now());
    update(registrationEmail);
  }

  public void sendEmailConfirmation(Registration registration, RegistrationEmail registrationEmail) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("link", environment.getProperty("link.login"));
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
      ).getOutText()
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
    model.put("account",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_ACCOUNT_EN.getOutTextKey(),
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
      ).getOutText()
    );

    EmailService.sendEmail(
      EmailService.getSession(),
      registration.getParticipant().getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RELEASED_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,!registration.getEvent().isActive()
    );

    registrationEmail.setReleasedSentDate(LocalDateTime.now());
    update(registrationEmail);

  }

  public void sendGenericEmail(List<Registration> registrationList, JsonObject emailContent) throws IOException, TemplateException {
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
//    Boolean allWithBundle =
//      emailContent.get("allWithBundle") != null ?
//        (
//          !emailContent.get("allWithBundle").isJsonNull() ?
//            emailContent.get("allWithBundle").getAsBoolean() : false
//        )
//        : false;
//    Boolean allWithLang =
//      emailContent.get("allWithLang") != null ?
//        (
//          !emailContent.get("allWithLang").isJsonNull() ?
//            emailContent.get("allWithLang").getAsBoolean() : false
//        )
//        : false;
//    Boolean allStatus =
//      emailContent.get("allStatus") != null ?
//        (
//          !emailContent.get("allStatus").isJsonNull() ?
//            emailContent.get("allStatus").getAsBoolean() : false
//        )
//        : false;

    log.info("subject: " + subject);
    log.info("content: " + content);
//    log.info("allWithBundle: " + allWithBundle);
//    log.info("allWithLang: " + allWithLang);
//    log.info("allStatus: " + allStatus);

    registrationList.forEach(registration -> {
//    String names = registrations
//      .stream()
//      .map(registration -> String.valueOf(registration.getParticipant().getFirstName()))
//      .collect(Collectors.joining(","));
    Map<String, Object> model = new HashMap<>();
    model.put("firstName", registration.getParticipant().getFirstName());
    model.put("content", content);
      Template template = null;
      try {
        template = mailConfig.getTemplate("generic.ftl");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      String languageKey =
        registration.getParticipant().getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
          registration.getParticipant().getPersonLang().getLanguageKey();

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
      ).getOutText()
    );

      try {
        EmailService.sendEmail(
          EmailService.getSession(),
          registration.getParticipant().getUser().getEmail(),
          subject,
          FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
          ,baseParService.testMailOnly()
          ,false
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (TemplateException e) {
        throw new RuntimeException(e);
      }

//      registrationEmail.setReleasedSentDate(LocalDateTime.now());
//    update(registrationEmail);

    });
  }

}
