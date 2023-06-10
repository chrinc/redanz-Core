package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.ProfileService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.web.security.config.JWTConfig;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RequiredArgsConstructor
@RestController
//@CrossOrigin(
//  origins = {
//    "http://localhost.ch/4200"
//    "https://redanz.ch"
//    ,"http://redanz.ch"
//    ,"https://register.stirit.ch"
//    ,"http://register.stirit.ch"
//    ,"https://stirit.ch"
//    ,"http://stirit.ch"
//    ,"http://stirit.redanz.ch"
//    ,"https://stirit.redanz.ch"
//  }
//  , allowedHeaders = "*"
//
//)
@RequestMapping("core-api/login")
public class LoginController {
  private final UserService userService;
  private final ProfileService profileService;

  private final RegistrationEmailService registrationEmailService;
  private final RegistrationService registrationService;

  @GetMapping(path = "/user-id")
  public Long userId(
    @RequestParam("email") String email
  ) {
    return userService.getUser(email).getUserId();
  }

  @GetMapping(path = "/user-is-tester")
  public boolean userIsTester(
    @RequestParam("userId") Long userId
  ) {
    try {
      return userService.userIsTester(userService.findByUserId(userId));
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/check-server")
  public Integer checkServer(
  ) {
    return 1;
  }

  @GetMapping(path = "/profile")
  public Person profile(
    @RequestParam("userId") Long userId
  ) {
    try {
      return profileService.getProfile(userId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/send-generic-email")
  public void sendGenericMail(
    @RequestParam("registrationId") Long registrationId,
    @RequestParam("emailContent") String emailContent
  ) {
    try {
      JsonObject jsonEmail = JsonParser.parseString(emailContent).getAsJsonObject();
      Boolean allWithBundle =
        jsonEmail.get("allWithBundle") != null ?
          (
            !jsonEmail.get("allWithBundle").isJsonNull() ?
              jsonEmail.get("allWithBundle").getAsBoolean() : false
          )
          : false;
      Boolean allWithLang =
        jsonEmail.get("allWithLang") != null ?
          (
            !jsonEmail.get("allWithLang").isJsonNull() ?
              jsonEmail.get("allWithLang").getAsBoolean() : false
          )
          : false;
      Boolean allStatus =
        jsonEmail.get("allStatus") != null ?
          (
            !jsonEmail.get("allStatus").isJsonNull() ?
              jsonEmail.get("allStatus").getAsBoolean() : false
          )
          : false;

      log.info("allWithBundle: " + allWithBundle);
      log.info("allWithLang: " + allWithLang);
      log.info("allStatus: " + allStatus);

      Registration registration = registrationService.findByRegistrationId(registrationId);

      List<Registration> registrationList = new ArrayList<Registration>();
      if (allWithBundle || allWithLang || allStatus) {
        registrationList = allStatus && allWithLang && allWithBundle ?
          registrationService.findAllByEventStatusLangAndBundle(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getParticipant().getPersonLang(), registration.getBundle()
          )
          : allStatus && allWithLang ?
          registrationService.findAllByEventStatusAndLang(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getParticipant().getPersonLang()
          )
          : allStatus && allWithBundle ?
          registrationService.findAllByEventStatusAndBundle(
            registration.getEvent(), registration.getWorkflowStatus(), registration.getBundle()
          )
          : allStatus ?
          registrationService.findAllByEventAndStatus(
            registration.getEvent(), registration.getWorkflowStatus()
          )
          : allWithLang && allWithBundle ?
          registrationService.findAllByEventLangAndBundle(
            registration.getEvent(), registration.getParticipant().getPersonLang(), registration.getBundle()
          )
          : allWithLang ?
          registrationService.findAllByEventAndLang(
            registration.getEvent(), registration.getParticipant().getPersonLang()
          )
          : allWithBundle ?
          registrationService.findAllByEventAndBundle(
            registration.getEvent(), registration.getBundle()
          )
          : null;

      } else {
        registrationList.add(registration);
      }

      registrationEmailService.sendGenericEmail(
        registrationList,
        jsonEmail
      );

    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
      try {
        String token = authorizationHeader.substring("Bearer ".length());
        Algorithm algorithm = Algorithm.HMAC256(JWTConfig.getJwtSecret().getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);
        String username = decodedJWT.getSubject();

        User user = userService.getUser(username);
        String refreshToken = JWT.create()
          .withSubject(user.getUsername())
          .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
          .withIssuer(request.getRequestURL().toString())
//          .withClaim("roles", user.getUserRole().toString().equals(GrantedAuthority::getAuthority).collect(Collectors.toList()))
          .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("access_token", refreshToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

      } catch (Exception exception) {
        response.setHeader("error", exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

      }
    } else {
      throw new RuntimeException("Refresh token is missing");
    }
  }
}
