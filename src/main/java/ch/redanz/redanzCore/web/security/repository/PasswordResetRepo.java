package ch.redanz.redanzCore.web.security.repository;

import ch.redanz.redanzCore.web.security.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepo extends JpaRepository<PasswordResetToken, Long> {
  PasswordResetToken findByToken(String token);
}
