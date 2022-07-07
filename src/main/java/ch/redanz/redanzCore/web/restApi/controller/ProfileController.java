package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.model.profile.service.*;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.*;

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

  @Autowired
  private Environment environment;

  @GetMapping("core-api/profile")
  public ResponseEntity<String> postForm() {
    return ResponseEntity.ok().body(userService.getRegistration());
  }

  @PostMapping(path = "/user/registration")
  public long register(@RequestBody UserResponse request) {
    userRegistrationService.register(request);
    return userService.getUser(request.getEmail()).getUserId();
  }

  @GetMapping(path = "/user/registration/confirm")
  public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
    userRegistrationService.confirmToken(token);
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(
      Objects.requireNonNull(environment.getProperty("link.email.confirmed"))
    )).build();
  }

  @GetMapping(path = "/country/all")
  public List<Country> getAllCountries() {
    return countryService.getAllCountries();
  }

  @GetMapping(path = "/reset-password-request")
  public void resetPasswordRequest(
    @RequestParam("email") String email
  ) {
    try {
      log.info("email?: {}", email);
      User user = userService.getUser(email);
      if (user == null) {
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_USER_NOT_FOUND_EN.getOutTextKey());
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
      log.info("token: {}", token);
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
      log.info("token: {}", token);
      log.info("password: {}", password);
      userRegistrationService.validatePasswordResetToken(token);
      passwordResetService.findByToken(token).getUser();
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
    profileService.registerProfile(userId, personResponse, link);
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
}
