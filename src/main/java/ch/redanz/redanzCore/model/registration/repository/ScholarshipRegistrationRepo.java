package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScholarshipRegistrationRepo extends JpaRepository<ScholarshipRegistration, Long> {
  ScholarshipRegistration findByRegistration(Registration registration);
  void deleteAllByRegistration(Registration registration);
}
