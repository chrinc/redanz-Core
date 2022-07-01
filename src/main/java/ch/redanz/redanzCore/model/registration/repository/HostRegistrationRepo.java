package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostRegistrationRepo extends JpaRepository<HostRegistration, Long> {
    HostRegistration findAllByRegistration(Registration registration);
}
