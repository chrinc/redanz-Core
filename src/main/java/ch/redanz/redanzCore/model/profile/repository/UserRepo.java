package ch.redanz.redanzCore.model.profile.repository;

import ch.redanz.redanzCore.model.profile.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepo extends JpaRepository<User, Long> {
  User findByUsername(String username);

  User findByUserId(Long userId);

  @Transactional
  @Modifying
  @Query("UPDATE User a " + "Set a.enabled = TRUE WHERE a.username = ?1")
  int enableUser(String email);
}

