package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.DonationRegistration;
import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRegistrationRepo extends JpaRepository<DonationRegistration, Long> {
  DonationRegistration findByRegistration(Registration registration);
}
