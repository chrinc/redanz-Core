package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HostRegistration;
import ch.redanz.redanzCore.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRegistrationRepository extends JpaRepository<HostRegistration, Long> {
    HostRegistration findAllByRegistration(Registration registration);
}
