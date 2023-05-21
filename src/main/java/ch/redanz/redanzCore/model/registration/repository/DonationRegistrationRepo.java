package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRegistrationRepo extends JpaRepository<DonationRegistration, Long> {
  DonationRegistration findByRegistration(Registration registration);
  void deleteAllByRegistration(Registration registration);
  List<DonationRegistration> findAllByRegistrationEvent(Event event);
}
