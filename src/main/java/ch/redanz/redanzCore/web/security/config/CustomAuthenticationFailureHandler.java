package ch.redanz.redanzCore.web.security.config;

import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import tools.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFailureHandler
  implements AuthenticationFailureHandler {
  private final JsonMapper objectMapper = JsonMapper.builderWithJackson2Defaults().build();

  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception) throws IOException {
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    Map<String, Object> data = new HashMap<>();
    data.put(
      "timestamp",
      Calendar.getInstance().getTime());
    data.put(
      "exception",
      OutTextConfig.LABEL_ERROR_AUTHENTICATION_EN.getOutTextKey());
    response.getOutputStream()
      .println(objectMapper.writeValueAsString(data));
  }
}
