package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationMatchingRepo extends JpaRepository<RegistrationMatching, Long> {

  Optional<RegistrationMatching> findByRegistration1(Registration registration1);

  boolean existsByRegistration2(Registration registration2);

  List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull();
}
