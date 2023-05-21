package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScholarshipRegistrationRepo extends JpaRepository<ScholarshipRegistration, Long> {
  ScholarshipRegistration findByRegistration(Registration registration);
  void deleteAllByRegistration(Registration registration);
  List<ScholarshipRegistration> findAllByRegistrationEvent(Event event);
}
