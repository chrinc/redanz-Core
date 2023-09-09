package ch.redanz.redanzCore.web.security.service;

import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.web.security.PasswordResetToken;
import ch.redanz.redanzCore.web.security.repository.PasswordResetRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@AllArgsConstructor
public class PasswordResetService {
  @Autowired
  Environment environment;


  private final UserService userService;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final PersonService personService;

  private final PasswordResetRepo passwordResetRepo;
  public void save(PasswordResetToken passwordResetToken) {
    passwordResetRepo.save(passwordResetToken);
  }

  public void createPasswordResetTokenForUser(User user, String token) {
    PasswordResetToken myToken =
      new PasswordResetToken(
        token,
        user,
        LocalDateTime.now().plusMinutes(
          Long.parseLong(
            Objects.requireNonNull(environment.getProperty("password-reset.token.expires"))
          )
        )
        );
    save(myToken);
  }

  public void updatePassword(User user, String password){
    String encodedPassword = bCryptPasswordEncoder.encode(password);
    user.setPassword(encodedPassword);

    if (personService.userHasPerson(user)) {
      user.setEnabled(true);
    }
    userService.save(user);
  }

  public PasswordResetToken findByToken(String token) {
    return passwordResetRepo.findByToken(token);
  }
}
