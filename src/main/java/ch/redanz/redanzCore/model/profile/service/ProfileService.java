package ch.redanz.redanzCore.model.profile.service;


import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.workshop.OutText;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.repository.OutTextRepo;
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
  private final PersonRepo personRepo;
  private final UserRepo userRepo;
  private final OutTextRepo outTextRepo;
  Configuration mailConfig;

  public void registerProfile(Long userId, PersonResponse personResponse, String registrationLink) throws IOException, TemplateException {

    Person newPerson = new Person(
      userService.findByUserId(userId),
      personResponse.getFirstName(),
      personResponse.getLastName(),
      personResponse.getStreet(),
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

  public void updateProfile(Long userId, PersonResponse personResponse){
    log.info("inc, before update person");
    Person updatePerson = personRepo.findByUser(userRepo.findByUserId(userId));
    updatePerson.setFirstName(personResponse.getFirstName());
    updatePerson.setLastName(personResponse.getLastName());
    updatePerson.setStreet(personResponse.getStreet());
    updatePerson.setPostalCode(personResponse.getPostalCode());
    updatePerson.setCity(personResponse.getCity());
    updatePerson.setCountry(countryService.findCountry(personResponse.getCountryId()));
    updatePerson.setUpdateTimestamp(LocalDateTime.now());
    log.info("inc, before save person");
    personService.savePerson(updatePerson);
    log.info("inc, save done");
  }

  public Person getProfile(Long userId){
     return personRepo.findByUser(userRepo.findByUserId(userId));
  }

  public HashMap getOutText() {
    HashMap outTextMap = new HashMap();
    List<OutTextConfig> profileOutText = new ArrayList<>();
    profileOutText.add(OutTextConfig.LABEL_ERROR_SUBMIT_GE);
    profileOutText.add(OutTextConfig.LABEL_ERROR_SUBMIT_EN);
    profileOutText.add(OutTextConfig.LABEL_ERROR_UNEXPECTED_GE);
    profileOutText.add(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN);
    profileOutText.add(OutTextConfig.LABEL_ERROR_USER_TAKEN_GE);
    profileOutText.add(OutTextConfig.LABEL_ERROR_USER_TAKEN_EN);
    profileOutText.add(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_GE);
    profileOutText.add(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN);
    profileOutText.forEach(outTextConfig -> {
      OutText outText =  (
        outTextRepo.findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(
          outTextConfig.getOutTextKey(),
          outTextConfig.getLanguageKey()
        )
      );

      outTextMap.put(
        outText.getOutTextId().getOutTextKey()  + "." + outText.getOutTextId().getOutTextLanguageKey(),
        outText.getOutText()
      );
    });
    return outTextMap;
  }
}
