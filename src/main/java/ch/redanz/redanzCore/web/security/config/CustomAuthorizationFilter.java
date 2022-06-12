package ch.redanz.redanzCore.web.security.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    if(request.getServletPath().equals("/core-api/login") || request.getServletPath().equals("/core-api/login/token/refresh")) {
      filterChain.doFilter(request, response);
    } else {
      String authorizationHeader = request.getHeader(AUTHORIZATION);
      log.info("inc@CustomAuthorizationFilter, authorizationHeader: {}", authorizationHeader);
      log.info("inc@CustomAuthorizationFilter, request.getServletPath(): {}", request.getServletPath());
      if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

        try {
          String token = authorizationHeader.substring("Bearer ".length());

          //  @todo lockup secret, keep it the same
          Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
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
//          response.addHeader("UserId", userId.toString());

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
