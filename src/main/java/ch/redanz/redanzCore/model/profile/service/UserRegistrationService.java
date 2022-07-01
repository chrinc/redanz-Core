package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.response.UserResponse;
import ch.redanz.redanzCore.service.email.EmailValidator;
import ch.redanz.redanzCore.web.security.ConfirmationToken;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  public String register(UserResponse request) {
    boolean isValidEmail = emailValidator.test(request.getEmail());

    if (!isValidEmail) {
      throw new IllegalStateException("email not valid");
    }

    String token = userService.signUpNewUser(
      new User(request.getEmail(),
        request.getPassword(),
        UserRole.PARTICIPANT) // @Todo variable?
    );
    return token;
  }

  @Transactional
  public String confirmToken(String token) {
    ConfirmationToken confirmationToken = confirmationTokenService
      .getToken(token)
      .orElseThrow(() ->
        new IllegalStateException("token not found")
      );

    if (confirmationToken.getConfirmedAt() != null) {
      throw new IllegalStateException("email already confirmed");
    }

    LocalDateTime expiredAt = confirmationToken.getExpiresAt();
    if (expiredAt.isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("token expired");
    }
    confirmationTokenService.setConfirmedAt(token);
    userService.enableUser(
      confirmationToken.getUser().getEmail()
    );
    return "confirmed";
  }
}
