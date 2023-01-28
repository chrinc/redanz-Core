package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeRegistrationRepo extends JpaRepository<HosteeRegistration, Long> {
  HosteeRegistration findByRegistration(Registration registration);
  List<HosteeRegistration> findAllByRegistrationEvent(Event event);
  void deleteAllByRegistration(Registration registration);

}
