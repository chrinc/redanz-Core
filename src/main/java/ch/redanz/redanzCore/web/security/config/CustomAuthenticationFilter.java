package ch.redanz.redanzCore.web.security.config;

import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


  private final AuthenticationManager authenticationManager;
  private AuthenticationFailureHandler failureHandler = new CustomAuthenticationFailureHandler();
  private RememberMeServices rememberMeServices = new NullRememberMeServices();

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws ApiRequestException {
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
    this.failureHandler = failureHandler;
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
    SecurityContextHolder.clearContext();
    this.rememberMeServices.loginFail(request, response);
    this.failureHandler.onAuthenticationFailure(request, response, exception);
  }

  @Override
  protected void successfulAuthentication(

    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authentication
  ) throws IOException {
    long MINUTES_300 = 300 * 60 * 1000L;
    User user = (User) authentication.getPrincipal();
    Algorithm algorithm = Algorithm.HMAC256(JWTConfig.getJwtSecret().getBytes());
//    log.info(user.getAuthorities().toString());
    String access_token = JWT.create()
      .withSubject(user.getUsername())
      .withExpiresAt(
        new Date(System.currentTimeMillis() + MINUTES_300)
      )
      .withIssuer(request.getRequestURL().toString())
      .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
      .sign(algorithm);

    String refresh_token = JWT.create()
      .withSubject(user.getUsername())
      .withExpiresAt(
        new Date(System.currentTimeMillis() + MINUTES_300)
      )
      .withIssuer(request.getRequestURL().toString())
      .sign(algorithm);
    Map<String, String> tokens = new HashMap<>();
    tokens.put("access_token", access_token);
    tokens.put("refresh_token", refresh_token);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
  }
}
