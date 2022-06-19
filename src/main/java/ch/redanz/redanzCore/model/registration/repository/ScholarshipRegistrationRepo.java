package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.DonationRegistration;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.ScholarshipRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScholarshipRegistrationRepo extends JpaRepository<ScholarshipRegistration, Long> {
  ScholarshipRegistration findByRegistration(Registration registration);
}
