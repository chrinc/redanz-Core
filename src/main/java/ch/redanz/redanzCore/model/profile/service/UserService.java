package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.repository.TestUserRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.web.security.ConfirmationToken;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j // for logs
@Transactional // no save required
public class UserService implements UserDetailsService {
//  private final static String USER_NOT_FOUND_MESSAGE = "user with ch.redanz.redanzCore.email %s not found";
  private final UserRepo userRepo;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;
  private final TestUserRepo testUserRepo;

  @Autowired
  private Environment environment;
//  @Value("${environment.jwt.token.expires}")
//  private final String tokenExpires;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepo.findByUsername(username);
    // log.info("inc load User by username");
    if (user == null) {
      throw new UsernameNotFoundException(OutTextConfig.LABEL_ERROR_USER_NOT_FOUND_EN.getOutTextKey());
    }
    try {
      return new org.springframework.security.core.userdetails.User(
        user.getUsername(),
        user.getPassword(),
        user.getEnabled(),
        user.isAccountNonExpired(),
        user.isCredentialsNonExpired(),
        user.isAccountNonLocked(),
        user.getAuthorities()
      );
    } catch(UsernameNotFoundException usernameNotFoundException) {
      throw new UsernameNotFoundException(OutTextConfig.LABEL_ERROR_USER_NOT_FOUND_EN.getOutTextKey());
    }
  }

  public boolean userIsAdmin(User user) {
    return user.getUserRole().equals(UserRole.ADMIN)
      || user.getUserRole().equals(UserRole.ORGANIZER);
  }

  public boolean userIsTester(User user) {
    return testUserRepo.existsByUsernameIgnoreCase(user.getUsername().replace(".", ""));
  }
  public boolean usernameIsTester(String username) {
    return testUserRepo.existsByUsernameIgnoreCase(username.replace(".", ""));
  }

  public void save(User user) {
    userRepo.save(user);
  }
  public void delete(User user) {userRepo.delete(user);
  }
  public User getUser(String username) {
    return userRepo.findByUsername(username);
  }

  public User findByUserId(Long userId) {
    return userRepo.findByUserId(userId);
  }

  public String getRegistration() {
    return "Please Fill In the  Registration Form!";
  }

  public String signUpNewUser(User user) {
    boolean userExists = userRepo.findByUsername(user.getUsername()) != null;
    if (userExists) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_USER_TAKEN_EN.getOutTextKey());
    }
    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    userRepo.save(user);
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
      token,
      LocalDateTime.now(),
      LocalDateTime.now().plusMinutes(
        Long.parseLong(
          Objects.requireNonNull(environment.getProperty("jwt.token.expires"))
        )
      ),
      user
    );
    confirmationTokenService.saveConfirmationToken(confirmationToken);
    return token;
  }

  public UserRole findUserRoleByName(String userRoleName) {
    return UserRole.valueOf(userRoleName);
  }

  public void changeUserRole(User user, UserRole userRole) {
    user.setUserRole(userRole);
    save(user);
  }

  public void enableUser(String username) {
    userRepo.enableUser(username);
  }

  public void setLoginTimestamp(User user) {
    user.setLoginTimestamp(LocalDateTime.now());
    user.setLogoutTimestamp(null);
    save(user);
  }

  public Long tokenExpiresInMillis() {
    return Long.parseLong(
      Objects.requireNonNull(
        environment.getProperty("jwt.auth-token.expires-min"))
    ) * 60 * 1000L;
  }

  public void logout(User user) {
    user.setLogoutTimestamp(LocalDateTime.now());
    save(user);
  }
}
