package ch.redanz.redanzCore.model.profile.repository;

import ch.redanz.redanzCore.model.profile.entities.Person;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {
  Person findByPersonId(Long personId);

  List<Person> findAllByUserUserRole(UserRole userRole);

  Person findByUser(User user);
  boolean existsByUser(User user);
}
