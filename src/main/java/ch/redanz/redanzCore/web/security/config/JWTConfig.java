package ch.redanz.redanzCore.web.security.config;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class JWTConfig {

  private static String jwtSecret;

  @Autowired
  public JWTConfig(
    @Value("${jwt.secret}")
    String jwtSecret
  ) {
    JWTConfig.jwtSecret = jwtSecret;
  }

  public static String getJwtSecret() {
    return JWTConfig.jwtSecret;
  }
}
