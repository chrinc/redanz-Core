package ch.redanz.redanzCore.model.profile.repository;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.TestUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestUserRepo extends JpaRepository<TestUser, Long> {
  // stored with or without dots, compared without dots (callers remove the dots)
  @Query(value = "SELECT COUNT(*) FROM test_user WHERE REPLACE(LOWER(username), '.', '') = LOWER(:username)", nativeQuery = true)
  long countByNormalizedUsername(@Param("username") String username);

  @Query(value = "SELECT COUNT(*) FROM test_user WHERE REPLACE(LOWER(email), '.', '') = LOWER(:email)", nativeQuery = true)
  long countByNormalizedEmail(@Param("email") String email);

  List<TestUser> findAllByOrderByEmailAsc();
}
