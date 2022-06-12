package ch.redanz.redanzCore.web.security.service;

import ch.redanz.redanzCore.web.security.ConfirmationToken;
import ch.redanz.redanzCore.model.profile.User;
import ch.redanz.redanzCore.web.security.repository.ConfirmationTokenRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
  private final ConfirmationTokenRepo confirmationTokenRepo;

  public void saveConfirmationToken(ConfirmationToken token){
    confirmationTokenRepo.save(token);
  }

  public Optional<ConfirmationToken> getToken(String token) {
    return confirmationTokenRepo.findByToken(token);
  }

  public String getTokenByUser(User user) {
    return confirmationTokenRepo.findFirstByUserOrderByCreatedAtDesc(user).getToken();
  }

  public int setConfirmedAt(String token) {
    return confirmationTokenRepo.updateConfirmerdAt(
      token,
      LocalDateTime.now()
    );
  }
}
