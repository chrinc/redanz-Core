package ch.redanz.redanzCore.web.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  private final AuthenticationManager authenticationManager;

  public CustomAuthenticationFilter(AuthenticationManager authenticationManager){
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    String username = request.getParameter("email");
    String password = request.getParameter("password");
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    return authenticationManager.authenticate(authenticationToken);
  }

  @Override
  protected void successfulAuthentication(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain chain,
    Authentication authentication
  ) throws IOException, ServletException {

    User user = (User)authentication.getPrincipal();
    Algorithm algorithm = Algorithm.HMAC256(JWTConfig.getJwtSecret().getBytes());
    String access_token = JWT.create()
      .withSubject(user.getUsername())
      .withExpiresAt(new Date(System.currentTimeMillis() + 10000 * 60 * 1000))
//      .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
      .withIssuer(request.getRequestURL().toString())
//            .withClaim("userId", )
      .withClaim("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
      .sign(algorithm);

    String refresh_token = JWT.create()
      .withSubject(user.getUsername())
      .withExpiresAt(new Date(System.currentTimeMillis() + 30000 * 60 * 1000))
//      .withExpiresAt(new Date(System.currentTimeMillis() + 30 * 60 * 1000))
      .withIssuer(request.getRequestURL().toString())
      .sign(algorithm);

//    response.setHeader("acces_token", access_token);
//    response.setHeader("refresh_token", refresh_token);

    Map<String, String> tokens = new HashMap<>();
    tokens.put("access_token", access_token);
    tokens.put("refresh_token", refresh_token);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//    log.info("inc, send response with token {}", tokens);
//    log.info("inc, send response with is authenticated {}", authentication.isAuthenticated());
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);


    // @todo, JAVA JWT 3.181 => MAVEN REPOSITORY
    // check out https://www.youtube.com/watch?v=VVn9OG9nfH0
    // for token generation
  }
}
