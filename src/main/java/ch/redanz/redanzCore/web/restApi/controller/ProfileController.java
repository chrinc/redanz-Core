package ch.redanz.redanzCore.web.restApi.controller;

import ch.redanz.redanzCore.model.profile.Country;
import ch.redanz.redanzCore.model.profile.Person;
import ch.redanz.redanzCore.model.profile.response.PersonResponse;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.model.profile.service.*;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import com.stripe.param.TokenCreateParams;
import freemarker.template.Configuration;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("core-api/profile")
public class ProfileController {
  private final CountryService countryService;
  private final OutTextService outTextService;
  private final UserService userService;
  private final ProfileService profileService;
  private final ConfirmationTokenService confirmationTokenService;
  private final UserRegistrationService userRegistrationService;

  @Autowired
  Configuration mailConfig;

  @Autowired
  private Environment environment;

  @GetMapping("core-api/profile")
  public ResponseEntity<String> postForm() {
    log.info("inc post successful");
    return ResponseEntity.ok().body(userService.getRegistration());
  }

  @PostMapping(path = "/user/registration")
  public long register(@RequestBody UserResponse request) {
    log.info("inc@user registration");
    userRegistrationService.register(request);
    return userService.getUser(request.getEmail()).getUserId();
  }

  @GetMapping(path = "/user/registration/confirm")
  public ResponseEntity<Void> confirm(@RequestParam("token") String token) {
    userRegistrationService.confirmToken(token);

    // @Todo, exception handler
    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(environment.getProperty("link.email.confirmed"))).build();
  }

  @GetMapping(path = "/country/all")
  public List<Country> getAllCountries() {
    log.info("inc, send getCountries");
    return countryService.getAllCountries();
  }
  @GetMapping(path = "/out-text/all")
  public HashMap getOutText() {
    ArrayList<String> types = new ArrayList<>() {
      {
        add("FRONT_BASE");
      }
    };

    log.info("inc, send getOutText: {}.", outTextService.getOutTextByType(types));
    return outTextService.getOutTextByType(types);
  }

  @PostMapping(path = "/person")
  public void register(
    @RequestParam("userId") Long userId,
    @RequestBody PersonResponse personResponse
  ) throws IOException, TemplateException {
    String link = environment.getProperty("link.confirm.token.prefix") + confirmationTokenService.getTokenByUser(userService.findByUserId(userId));
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
