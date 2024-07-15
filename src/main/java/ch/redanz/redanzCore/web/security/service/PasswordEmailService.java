package ch.redanz.redanzCore.web.security.service;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.service.email.EmailService;
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
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PasswordEmailService {
  private final OutTextService outTextService;
  private final LanguageService languageService;
  private final PersonService personService;
  private final BaseParService baseParService;
  private final EmailService emailService;


  @Autowired
  Environment environment;

  @Autowired
  Configuration mailConfig;

  public void sendResetPasswordEmail(
    User user,
    String link
  ) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    Person person = personService.findByUser(user);
    model.put("headerLink", environment.getProperty("link.login"));
    model.put("loginLink", environment.getProperty("link.login") + "/" + person.getPersonLang().getLanguageKey().toLowerCase());

    model.put("firstName", person.getFirstName());
    Template template = mailConfig.getTemplate("resetPassword.ftl");

    String languageKey =
      person.getPersonLang() == null ?
        languageService.findLanguageByLanguageKey("GE").getLanguageKey() :
        person.getPersonLang().getLanguageKey();

    model.put("base",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RESET_PASSWORD_BASE_GE.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("reset_now",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_PASSWORD_RESET_NOW_EN.getOutTextKey(),
        languageKey
      ).getOutText()
    );
    model.put("expires",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RESET_PASSWORD_LINK_EXPIRES_EN.getOutTextKey(),
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
    // log.info("set team");
    // log.info("baseParService.organizerName(): " + baseParService.organizerName());
    model.put("team",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(),
        languageKey
      ).getOutText().replace("{1}", baseParService.organizerName())
    );
    emailService.sendEmail(
//      EmailService.getSession(),
      user.getUsername(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_RESET_PASSWORD_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
      ,baseParService.testEmail()
      ,false
      ,null
    );
  }
}
