package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VolunteerRegistrationRepo extends JpaRepository<VolunteerRegistration, Long> {
  VolunteerRegistration findByRegistration (Registration registration);
}
