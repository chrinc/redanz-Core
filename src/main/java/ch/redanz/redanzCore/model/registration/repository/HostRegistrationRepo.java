package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostRegistrationRepo extends JpaRepository<HostRegistration, Long> {
    HostRegistration findAllByRegistration(Registration registration);
    List<HostRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, boolean active);
    void deleteAllByRegistration(Registration registration);
}
