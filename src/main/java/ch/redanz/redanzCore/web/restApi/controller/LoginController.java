package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.service.ProfileService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.web.security.config.JWTConfig;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = {"https://redanz.ch", "http://localhost.ch/4200", "http://redanz.ch"}, allowedHeaders = "*")
@RequestMapping("core-api/login")
public class LoginController {
  private final UserService userService;
  private final ProfileService profileService;

  @GetMapping(path = "/user_id")
  public Long getUserId(
    @RequestParam("email") String email
  ) {
    return userService.getUser(email).getUserId();
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
