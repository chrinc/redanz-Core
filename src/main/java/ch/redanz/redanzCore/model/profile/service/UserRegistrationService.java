package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.service.email.EmailValidator;
import ch.redanz.redanzCore.web.security.ConfirmationToken;
import ch.redanz.redanzCore.web.security.PasswordResetToken;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import ch.redanz.redanzCore.web.security.service.PasswordResetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class UserRegistrationService {
  private final EmailValidator emailValidator;
  private final UserService userService;
  private final ConfirmationTokenService confirmationTokenService;
  private final PasswordResetService passwordResetService;
  private final PersonService personService;

  @Autowired
  private Environment environment;

  public String register(UserResponse request) {
    boolean isValidEmail = emailValidator.test(request.getUsername());

    if (!isValidEmail) {
      throw new IllegalStateException("email not valid");
    }

    String token = userService.signUpNewUser(
      new User(
        request.getUsername(),
        request.getPassword(),
        UserRole.PARTICIPANT
      )
    );
    return token;
  }

  @Transactional
  public String confirmToken(String token) {
    String baseUrl = "/email/confirmed";
    ConfirmationToken confirmationToken = confirmationTokenService
      .getToken(token)
      .orElseThrow(() ->
        new IllegalStateException("token not found")
      );
    String lang = personService.findByUser(confirmationToken.getUser()).getPersonLang().getLanguageKey().toLowerCase();
    if (confirmationToken.getConfirmedAt() != null) {
      return
        baseUrl
          + "/" + OutTextConfig.LABEL_ERROR_EMAIL_CONFIRMED_EN.getOutTextKey().toLowerCase()
          + "/" + lang;
    }

    LocalDateTime expiredAt = confirmationToken.getExpiresAt();
    if (expiredAt.isBefore(LocalDateTime.now())) {
      userService.delete(confirmationToken.getUser());
      personService.delete(personService.findByUser(confirmationToken.getUser()));
      return
        baseUrl
          + "/" + OutTextConfig.LABEL_ERROR_EMAIL_CONFIRMED_EN.getOutTextKey().toLowerCase()
          + "/" + lang;
    }
    confirmationTokenService.setConfirmedAt(token);
    userService.enableUser(
      confirmationToken.getUser().getUsername()
    );
    return
      baseUrl
        + "/" + OutTextConfig.LABEL_EMAIL_CONFIRMED_EN.getOutTextKey().toLowerCase()
        + "/" + lang;
  }

  @Transactional
  public boolean validatePasswordResetToken(String token) {
    try {
      final PasswordResetToken passToken = passwordResetService.findByToken(token);

      if (isTokenFound(passToken)
        && !isTokenExpired(passToken)) {
        return true;
      } else throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  private boolean isTokenFound(PasswordResetToken passToken) {
    return passToken != null;
  }

  private boolean isTokenExpired(PasswordResetToken passToken) {
    return passToken.getExpiresAt().isBefore(LocalDateTime.now());
  }
}
