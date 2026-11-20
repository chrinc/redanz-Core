package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationPayment;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationPaymentRepo extends JpaRepository<RegistrationPayment, Long> {
  List<RegistrationPayment> findAllByRegistration(Registration registration);
  List<RegistrationPayment> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  void deleteAllByRegistration(Registration registration);
}

