package ch.redanz.redanzCore.model.profile.service;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.service.email.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileService {
  private final PersonService personService;
  private final UserService userService;
  private final CountryService countryService;
  private final OutTextService outTextService;
  private final LanguageService languageService;
  private final BaseParService baseParService;
  Configuration mailConfig;

  public void registerProfile(Long userId, PersonResponse personResponse, String registrationLink) throws IOException, TemplateException {
    Person newPerson = new Person(userService.findByUserId(userId), personResponse.getFirstName(), personResponse.getLastName(), personResponse.getStreet(), personResponse.getPostalCode(), personResponse.getCity(), countryService.findCountry(personResponse.getCountryId()), languageService.findLanguageByLanguageKey(personResponse.getLanguage()));
    newPerson.setUpdateTimestamp(LocalDateTime.now());
    personService.addPerson(newPerson);
    sendRegisteredProfileEmail(newPerson, registrationLink);
  }

  public void updateProfile(Long userId, PersonResponse personResponse) {
    Person updatePerson = personService.findByUser(userService.findByUserId(userId));
    updatePerson.setFirstName(personResponse.getFirstName());
    updatePerson.setLastName(personResponse.getLastName());
    updatePerson.setStreet(personResponse.getStreet());
    updatePerson.setPostalCode(personResponse.getPostalCode());
    updatePerson.setCity(personResponse.getCity());
    updatePerson.setCountry(countryService.findCountry(personResponse.getCountryId()));
    updatePerson.setUpdateTimestamp(LocalDateTime.now());
    personService.savePerson(updatePerson);
  }

  public Person getProfile(Long userId) {
    return personService.findByUser(userService.findByUserId(userId));
  }

  public void sendRegisteredProfileEmail(Person person, String registrationLink) throws IOException, TemplateException {
    String languageKey = person.getPersonLang() == null ? languageService.findLanguageByLanguageKey("GE").getLanguageKey() : person.getPersonLang().getLanguageKey();
    Map<String, Object> model = new HashMap<>();
    model.put("link", registrationLink);
    model.put("firstName", person.getFirstName());
    model.put("base", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_BASE_EN.getOutTextKey(), languageKey).getOutText());
    model.put("activate_now", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW_EN.getOutTextKey(), languageKey).getOutText());
    model.put("expires", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES_EN.getOutTextKey(), languageKey).getOutText());
    model.put("regards", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(), languageKey).getOutText());
    model.put("see_you", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(), languageKey).getOutText());
    model.put("team", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(), languageKey).getOutText());
    Template template = mailConfig.getTemplate("profileReceived.ftl");

    EmailService.sendEmail(
      EmailService.getSession(),
      person.getUser().getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CONFIRM_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,baseParService.testMailOnly()
    );
  }
}
