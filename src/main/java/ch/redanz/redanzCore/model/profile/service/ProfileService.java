package ch.redanz.redanzCore.model.profile.service;


import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.service.email.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProfileService {
  private final PersonService personService;
  private final UserService userService;
  private final CountryService countryService;
  Configuration mailConfig;

  public void registerProfile(Long userId, PersonResponse personResponse, String registrationLink) throws IOException, TemplateException {

    Person newPerson = new Person(
      userService.findByUserId(userId),
      personResponse.getFirstName(),
      personResponse.getLastName(),
      personResponse.getStreet(),
      personResponse.getStreetNumber(),
      personResponse.getPostalCode(),
      personResponse.getCity(),
      countryService.findCountry(personResponse.getCountryId())

    );

    newPerson.setUpdateTimestamp(LocalDateTime.now());
    personService.addPerson(newPerson);

    Map<String, Object> model = new HashMap<>();
    model.put("link", registrationLink);
    model.put("firstName", newPerson.getFirstName());
    Template template = mailConfig.getTemplate("profileReceived.ftl");

//    EmailService emailService = new EmailService();
    EmailService.sendEmail(
      EmailService.getSession(),
      userService.findByUserId(userId).getEmail(),
      "Email Confirmation",
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }


}
