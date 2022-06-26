package ch.redanz.redanzCore.web.security.config;

import ch.redanz.redanzCore.model.profile.UserRole;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
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
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@AllArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String baseUrl = "http://redanz.ch";
    String baseUrl2 = "https://redanz.ch";
    log.info("inc, request.getServletPath(): {}",request.getServletPath());
    log.info("inc, request.getServletPath() equals: {}",request.getServletPath().equals("/core-api/zahls/checkout/confirm"));
    log.info("inc, request.getServletPath() ==: {}",request.getServletPath() == "/core-api/zahls/checkout/confirm");
    if(
         request.getServletPath().equals("/core-api/login")
      || request.getServletPath().equals("/core-api/login/token/refresh")
      || request.getServletPath().equals("/core-api/zahls/checkout/confirm")
    ) {
      filterChain.doFilter(request, response);
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      log.info("inc@CustomAuthorizationFilter, authorizationHeader: {}", authorizationHeader);
      log.info("inc@CustomAuthorizationFilter, request.getServletPath(): {}", request.getServletPath());
      log.info("inc, header: allow Origin? {}", request.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
      log.info("inc, header: allow request URL {}", request.getRequestURL());
      if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {


        log.info("inc, get user Id? {}", request.getParameter("userId"));


        try {
          String token = authorizationHeader.substring("Bearer ".length());

          Algorithm algorithm = Algorithm.HMAC256(JWTConfig.getJwtSecret().getBytes());
          JWTVerifier verifier = JWT.require(algorithm).build();
          DecodedJWT decodedJWT = verifier.verify(token);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
//          Long userId = decodedJWT.getClaim("userId").asLong();
          Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//          log.info("inc, regular response: {}", response);
          stream(roles).forEach(role ->{
            authorities.add(new SimpleGrantedAuthority(role));
          });

          // only allow retrieve own user data
          Long requestUserId = request.getParameter("userId") == null ? null : Long.valueOf(request.getParameter("userId"));
          if (requestUserId != null && !Objects.equals(userService.getUser(username).getUserId(), requestUserId)) {
            authorities.stream().filter(auth -> auth.getAuthority().equals(UserRole.ORGANIZER.name()))
              .findAny()
              .orElseThrow(
                () -> new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey())
              );
          }

          // only organizers are authorized to retreive report data
          if (request.getServletPath().startsWith("/core-api/app/report")) {
             authorities.stream().filter(auth -> auth.getAuthority().equals(UserRole.ORGANIZER.name()))
               .findAny()
               .orElseThrow(
                 () -> new ApiRequestException(OutTextConfig.LABEL_ERROR_UNAUTHORIZED_EN.getOutTextKey())
               );
          }


          UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//          log.info("inc@customAuthFilter, response_ {}", response.getHeaderNames());
          filterChain.doFilter(request, response);
        } catch (Exception exception) {
//          log.error("Error logging in: {}", exception.getMessage());
          response.setHeader("error", exception.getMessage());
          response.setStatus(FORBIDDEN.value());
//          response.sendError(FORBIDDEN.value());
          Map<String, String> error = new HashMap<>();
          error.put("error_message", exception.getMessage());
          response.setContentType(MediaType.APPLICATION_JSON_VALUE);
          new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
      } else {
        log.info("Authorized, public, response: {}", response);
        filterChain.doFilter(request, response);
      }
    }
  }
}
