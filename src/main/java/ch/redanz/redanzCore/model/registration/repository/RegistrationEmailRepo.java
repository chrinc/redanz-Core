package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationEmailRepo extends JpaRepository<RegistrationEmail, Long> {
  RegistrationEmail findByRegistration(Registration registration);
}
