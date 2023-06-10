package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerRegistrationRepo extends JpaRepository<VolunteerRegistration, Long> {
  VolunteerRegistration findByRegistration (Registration registration);
  List<VolunteerRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, boolean active);
  void deleteAllByRegistration(Registration registration);
}
