package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.model.profile.service.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RequiredArgsConstructor
@RestController
@Slf4j
//@CrossOrigin(origins="http://localhost:4200", allowedHeaders = "*")

@CrossOrigin(origins="*", allowedHeaders = "*")
@RequestMapping("core-api/login")
public class LoginController {
  private final UserService userService;

  @GetMapping(path="/user_id")
  public Long getUserId(
          @RequestParam("email") String email
  ) {
    log.info("inc, send getAllTracks: {}.", userService.getUser(email));
    return userService.getUser(email).getUserId();
  }

  @GetMapping("/token/refresh")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
    String authorizationHeader = request.getHeader(AUTHORIZATION);
    if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

      try {
        String token = authorizationHeader.substring("Bearer ".length());

        //  @todo lockup secret, keep it the same
        Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
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
        log.error("Error logging in: {}", exception.getMessage());
        response.setHeader("error", exception.getMessage());
        response.setStatus(FORBIDDEN.value());
//          response.sendError(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        new ObjectMapper().writeValue(response.getOutputStream(), error);

      }
    } else {
      log.error("Else response: {}", response);
      throw new RuntimeException("Refresh token is missing");
    }
  }
}
