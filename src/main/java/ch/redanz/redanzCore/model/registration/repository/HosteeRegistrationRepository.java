package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HostRegistration;
import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HosteeRegistrationRepository extends JpaRepository<HosteeRegistration, Long> {
  HosteeRegistration findByRegistration(Registration registration);

}
