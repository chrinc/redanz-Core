package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.entities.Country;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.model.profile.service.CountryService;
import ch.redanz.redanzCore.model.profile.service.ProfileService;
import ch.redanz.redanzCore.model.profile.service.UserRegistrationService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
