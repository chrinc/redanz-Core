package ch.redanz.redanzCore.model.profile.service;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
  private final RegistrationService registrationService;
  private final EmailService emailService;
  Configuration mailConfig;

  @Autowired
  Environment environment;

  public void registerProfile(Long userId, String username, Map<String, Object> personResponse, String registrationLink, String headerLink) throws IOException, TemplateException {
    Person newPerson = new Person(
      userService.findByUserId(userId)
      ,personResponse.get("firstName").toString()
      ,personResponse.get("lastName").toString()
      ,personResponse.get("street").toString()
      ,personResponse.get("postalCode").toString()
      ,personResponse.get("city").toString()
      ,countryService.findCountry(Long.valueOf(personResponse.get("countryId").toString()))
      ,languageService.findLanguageByLanguageKey(personResponse.get("language").toString())
      ,username
      ,true
    );

    newPerson.setUpdateTimestamp(LocalDateTime.now());
    personService.addPerson(newPerson);
    sendRegisteredProfileEmail(newPerson, registrationLink, headerLink);
  }

  public void updateProfile(Long userId, PersonResponse personResponse) {
    Person updatePerson = personService.findByUser(userService.findByUserId(userId));
    updatePerson.setFirstName(personResponse.getFirstName());
    updatePerson.setLastName(personResponse.getLastName());
    updatePerson.setStreet(personResponse.getStreet());
    updatePerson.setPostalCode(personResponse.getPostalCode());
    updatePerson.setCity(personResponse.getCity());
    updatePerson.setCountry(countryService.findCountry(personResponse.getCountryId()));
    updatePerson.setPersonLang(languageService.findLanguageByLanguageKey(personResponse.getLanguage()));
    updatePerson.setUpdateTimestamp(LocalDateTime.now());
    personService.savePerson(updatePerson);
  }

  public void remove(Person person) {
    if (person.getUser() != null) {
      userService.delete(person.getUser());
      person.setUser(null);
      person.setEmail(null);
    }

    person.setActive(false);
    personService.savePerson(person);
  }

  public Person getProfile(Long personId) {
    return personService.findByPersonId(personId);
  }

  public Person getProfile(String username) {
    return personService.findByUser(userService.getUser(username));
  }

  public List<Person> getPersons() {
    return personService.findAll(true);
  }

  public List<Person> getPersonsWoutRegistration(Event event, Person person) {
    List<Person> personsWoutRegistration = new ArrayList<>();
    personService.findAll(true).forEach(foundPerson -> {
      if (!registrationService.hasRegistration(event, foundPerson) || foundPerson == person) {
        personsWoutRegistration.add(foundPerson);
      }
    });
    return personsWoutRegistration;
  }

  public void sendRegisteredProfileEmail(Person person, String registrationLink, String headerLink) throws IOException, TemplateException {
    String languageKey = person.getPersonLang() == null ? languageService.findLanguageByLanguageKey("GE").getLanguageKey() : person.getPersonLang().getLanguageKey();
    Map<String, Object> model = new HashMap<>();
    model.put("headerLink", environment.getProperty("link.login") + "/assets/graphics/" + environment.getProperty("oms.host.key"));
    model.put("registrationLink", registrationLink);
    model.put("firstName", person.getFirstName());

    model.put("omsFbLink", environment.getProperty("oms.fb.link"));
    model.put("omsInstaLink", environment.getProperty("oms.insta.link"));
    model.put("omsHostDomain", environment.getProperty("oms.host.domain"));
    model.put("omsHostName", environment.getProperty("oms.host.name"));
    model.put("hostEmail", environment.getProperty("email.host.email"));

    model.put("base", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_BASE_EN.getOutTextKey(), languageKey).getOutText());
    model.put("activate_now", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_ACTIVATE_NOW_EN.getOutTextKey(), languageKey).getOutText());
//    model.put("expires", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_CONFIRM_EMAIL_LINK_EXPIRES_EN.getOutTextKey(), languageKey).getOutText());
    model.put("regards", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_REGARDS_EN.getOutTextKey(), languageKey).getOutText());
    model.put("see_you", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_SEE_YOU_EN.getOutTextKey(), languageKey).getOutText());
    model.put(
      "team", outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_EMAIL_TEAM_EN.getOutTextKey(), languageKey).getOutText().replace("{1}", environment.getProperty("oms.host.name"))
    );
    Template template = mailConfig.getTemplate("profileReceived.ftl");

    model.put("changeLanguage",
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_CHANGE_LANGUAGE_EN.getOutTextKey(),
        languageKey).getOutText().replace("{1}", environment.getProperty("link.login") + "/app/login/profile")
    );

    emailService.sendEmail(
      person.getEmail(),
      outTextService.getOutTextByKeyAndLangKey(
        OutTextConfig.LABEL_EMAIL_CONFIRM_SUBJECT_EN.getOutTextKey(),
        languageKey
      ).getOutText(),
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
      ,userService.emailIsTester(person.getEmail())
      ,false
      ,null
    );
  }
}
