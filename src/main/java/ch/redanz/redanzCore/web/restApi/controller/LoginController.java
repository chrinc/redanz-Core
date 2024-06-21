package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.ProfileService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.web.security.config.JWTConfig;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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
@RequestMapping("core-api/login")
public class LoginController {
  private final UserService userService;
  private final ProfileService profileService;
  private final PersonService personService;

  @GetMapping(path = "/user-id")
  public Long userId(
    @RequestParam("username") String username
  ) {
    return userService.getUser(username).getUserId();
  }

  @GetMapping(path = "/person-id")
  public Long personId(
    @RequestParam("username") String username
  ) {
    return personService.findByUser(userService.getUser(username)).getPersonId();
  }

  @GetMapping(path = "/person")
  public Person person(
    @RequestParam("personId") Long personId
  ) {
    try {
      return profileService.getProfile(personId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/person-by-username")
  public Person getPersonByUserName(
    @RequestParam("username") String username
  ) {
    try {
      return profileService.getProfile(username);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
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
