package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.model.profile.service.*;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import ch.redanz.redanzCore.web.security.service.PasswordEmailService;
import ch.redanz.redanzCore.web.security.service.PasswordResetService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("core-api/profile")
public class ProfileController {
  private final CountryService countryService;
  private final OutTextService outTextService;
  private final UserService userService;
  private final ProfileService profileService;
  private final ConfirmationTokenService confirmationTokenService;
  private final UserRegistrationService userRegistrationService;
  private final PasswordEmailService passwordEmailService;
  private final PasswordResetService passwordResetService;
  private final PersonService personService;
  private final LanguageService languageService;

  @Autowired
  private Environment environment;

  @GetMapping("core-api/profile")
  public ResponseEntity<String> postForm() {
    return ResponseEntity.ok().body(userService.getRegistration());
  }

  @GetMapping("/languages")
  public List<Language> getLanguages() {
    return languageService.findAll();
  }

  @PostMapping(path = "/user/registration")
  public long register(@RequestBody UserResponse request) {
    userRegistrationService.register(request);
    return userService.getUser(request.getUsername()).getUserId();
  }

  @GetMapping(path = "/user/registration/confirm")
  public String confirm(@RequestParam("token") String token) {
    try {
      String link = userRegistrationService.confirmToken(token);
      log.info("link");
      log.info(link);
      return link;
    } catch (ApiRequestException apiRequestException) {
      log.info("inc@apiRequestException");
      log.info(apiRequestException.getMessage());
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      log.info("inc@exception");
      log.info(exception.getMessage());
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
//  @GetMapping(path = "/user/registration/confirm")
//  public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
//    String link = userRegistrationService.confirmToken(token);
//    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(
//      Objects.requireNonNull(link)
//    )).build();
//  }

  @GetMapping(path = "/country/all")
  public List<Country> getAllCountries() {
    return countryService.getAllCountries();
  }

  @GetMapping(path = "/reset-password-request")
  public void resetPasswordRequest(
    @RequestParam("email") String email
  ) {
//    log.info("email: "+ email);
    try {
      User user = userService.getUser(email);
      if (user == null) {

        // do not inform user about missing user in database.
        return;
        // throw new ApiRequestException(OutTextConfig.LABEL_ERROR_USER_NOT_FOUND_EN.getOutTextKey());
      }

      String token = UUID.randomUUID().toString();
      String languageKey = personService.findByUser(user).getPersonLang().getLanguageKey();
      passwordResetService.createPasswordResetTokenForUser(user, token);
      String link = environment.getProperty("link.reset.password.prefix") + "/" + token + "/" + languageKey.toLowerCase();
      passwordEmailService.sendResetPasswordEmail(user, link);

    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/check-password-token")
  public void checkPasswordToken(
    @RequestParam("token") String token) {
    try {
      userRegistrationService.validatePasswordResetToken(token);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @PostMapping(path = "/reset-password")
  public void resetPassword(
    @RequestParam("token") String token,
    @RequestParam("password") String password
  ) {
    try {
      userRegistrationService.validatePasswordResetToken(token);
      passwordResetService.updatePassword(passwordResetService.findByToken(token).getUser(), password);
    } catch (ApiRequestException apiRequestException) {
      throw new ApiRequestException(apiRequestException.getMessage());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }


  @GetMapping(path = "/out-text/all")
  public HashMap<String, String> getOutText() {
    ArrayList<String> types = new ArrayList<>() {
      {
        add("FRONT_BASE");
      }
    };
    return outTextService.getOutTextByType(types);
  }

  @PostMapping(path = "/person")
  public void register(
    @RequestParam("userId") Long userId,
    @RequestBody PersonResponse personResponse
  ) throws IOException, TemplateException {
    String link = environment.getProperty("link.confirm.token.prefix")
      + confirmationTokenService.getTokenByUser(userService.findByUserId(userId)
    );

    String headerLink = environment.getProperty("email.header.link");
    profileService.registerProfile(userId, personResponse, link, headerLink);
  }

  @GetMapping(path = "/persons")
  public List<Person> persons(
    @RequestParam("userId") Long userId
  ) {
    return profileService.getPersons();
  }

  @PostMapping(path = "/person/update")
  public void updatePerson(
    @RequestParam("userId") Long userId,
    @RequestBody PersonResponse personResponse
  ) {
    try {
      profileService.updateProfile(userId, personResponse);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/person/remove")
  public void removePerson(
    @RequestParam("personId") Long personId
  ) {
    try {
      profileService.remove(personService.findByPersonId(personId));
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/person/lang")
  @Transactional
  public void updateLanguage(
    @RequestParam("userId") Long userId,
    @RequestParam("languageKey") String languageKey
  ) {
    try {
      personService.findByUser(userService.findByUserId(userId)).setPersonLang(languageService.findLanguageByLanguageKey(languageKey.toUpperCase()));
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }
}
