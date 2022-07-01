package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HosteeRegistrationRepo extends JpaRepository<HosteeRegistration, Long> {
  HosteeRegistration findByRegistration(Registration registration);

}
