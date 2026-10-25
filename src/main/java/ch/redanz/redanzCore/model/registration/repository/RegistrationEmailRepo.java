package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationEmailRepo extends JpaRepository<RegistrationEmail, Long> {
  RegistrationEmail findByRegistration(Registration registration);
  List<RegistrationEmail> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
}
