package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRegistrationRepo extends JpaRepository<DonationRegistration, Long> {
  DonationRegistration findByRegistration(Registration registration);
}
