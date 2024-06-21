package ch.redanz.redanzCore.web.security.config;

import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.service.log.ErrorLogService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@AllArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  private final UserService userService;
  private final RegistrationService registrationService;
  private final PersonService personService;
  private final ErrorLogService errorLogService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(
         request.getServletPath().equals("/core-api/login")
      || request.getServletPath().equals("/core-api/login/token/refresh")
      || request.getServletPath().equals("/core-api/zahls/checkout/confirm")
    ) {
      filterChain.doFilter(request, response);
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        try {
          String token = authorizationHeader.substring("Bearer ".length());

          Algorithm algorithm = Algorithm.HMAC256(JWTConfig.getJwtSecret().getBytes());
          JWTVerifier verifier = JWT.require(algorithm).build();
          DecodedJWT decodedJWT = verifier.verify(token);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
          stream(roles).forEach(role ->{
            authorities.add(new SimpleGrantedAuthority(role));
          });


          // except you are admin or organizer
          Long requestUserId = request.getParameter("userId") == null ? null : Long.valueOf(request.getParameter("userId"));
          Long requestPersonId = request.getParameter("personId") == null ? null : Long.valueOf(request.getParameter("personId"));
          Long requestRegistrationId = request.getParameter("registrationId") == null ? null : Long.valueOf(request.getParameter("registrationId"));

//          log.info("inc@auth,  request.getServletPath() {}",  request.getServletPath());
//          log.info("inc@auth, requestUserId {}", requestUserId);
//          log.info("inc@auth, requestPersonId {}", requestPersonId);
//          log.info("inc@auth, requestRegistrationId {}", requestRegistrationId);
//          log.info("inc@auth, username {}", username);
          if (
               (requestUserId != null && !Objects.equals(userService.getUser(username).getUserId(), requestUserId))
            || (requestPersonId != null && !Objects.equals(personService.findByUser(userService.getUser(username)).getPersonId(), requestPersonId))
            || (
              requestRegistrationId != null
                && !Objects.equals(
                personService.findByUser(userService.getUser(username)).getPersonId(),
                registrationService.findByRegistrationId(requestRegistrationId).getParticipant().getPersonId()
              )
            )
          ) {
            authorities.stream().filter(
              auth ->
                   auth.getAuthority().equals(UserRole.ORGANIZER.name())
                || auth.getAuthority().equals(UserRole.ADMIN.name())
              )
              .findAny()
              .orElseThrow(
                () -> new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey())
              );
          }
          // only organizers are authorized to retrieve report data
          if (request.getServletPath().startsWith("/core-api/app/report")) {

             authorities.stream().filter(
               auth ->
                    auth.getAuthority().equals(UserRole.ORGANIZER.name())
                 || auth.getAuthority().equals(UserRole.ADMIN.name())
               )
               .findAny()
               .orElseThrow(
                 () -> new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey())
               );
          }
          if (request.getServletPath().startsWith("/core-api/app/jobs")) {
            authorities.forEach(simpleGrantedAuthority -> {
//              log.info(simpleGrantedAuthority.getAuthority());
            });
             authorities.stream().filter(
               auth ->
                    auth.getAuthority().equals(UserRole.ORGANIZER.name())
                 || auth.getAuthority().equals(UserRole.ADMIN.name())
               )
               .findAny()
               .orElseThrow(
                 () -> new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey())
               );
          }
          UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request, response);
        } catch (Exception exception) {
          errorLogService.addLog("AUTH", exception.getMessage());
          response.setHeader("error", exception.getMessage());
          response.setStatus(FORBIDDEN.value());
          Map<String, String> error = new HashMap<>();
          error.put("error_message", exception.getMessage());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
      } else {
        filterChain.doFilter(request, response);
      }
    }
  }
}
