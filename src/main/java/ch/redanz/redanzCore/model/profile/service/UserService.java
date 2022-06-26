package ch.redanz.redanzCore.model.profile.service;

import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.web.security.service.ConfirmationTokenService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.web.security.ConfirmationToken;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j // for logs
@Transactional // no save required
public class UserService implements UserDetailsService {
  private final static String USER_NOT_FOUND_MESSAGE = "user with ch.redanz.redanzCore.email %s not found";
  private final UserRepo userRepo;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final ConfirmationTokenService confirmationTokenService;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepo.findByEmail(email);
      if (user == null) {
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_USER_NOT_FOUND_EN.getOutTextKey());
      }

      return new org.springframework.security.core.userdetails.User(
        user.getEmail(),
        user.getPassword(),
        user.getEnabled(),
        user.isAccountNonExpired(),
        user.isCredentialsNonExpired(),
        user.isAccountNonLocked(),
        user.getAuthorities()
      );
  }

  public User getUser(String email) {
    return userRepo.findByEmail(email);
  }
  public User findByUserId(Long userId) {
    return userRepo.findByUserId(userId);
  }
  public String getRegistration(){
    return "Please Fill In the  Registration Form!";
  }
  public String signUpUser(User user) {
    boolean userExists = userRepo.findByEmail(user.getEmail()) != null;
//    log.info("inc, signUpUser user: {}.", user);

    if(userExists) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_USER_TAKEN_EN.getOutTextKey());
    }
    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);


    userRepo.save(user);

    // todo: constant 15 minutes?
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
      token,
      LocalDateTime.now(),
      LocalDateTime.now().plusMinutes(15),
            user
    );
    confirmationTokenService.saveConfirmationToken(confirmationToken);

    return token;
  }
  public int enableUser(String email) {
    return userRepo.enableUser(email);
  }
}
