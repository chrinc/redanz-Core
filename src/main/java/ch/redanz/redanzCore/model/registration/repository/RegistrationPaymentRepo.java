package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationPayment;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationPaymentRepo extends JpaRepository<RegistrationPayment, Long> {
  List<RegistrationPayment> findAllByRegistration(Registration registration);
  void deleteAllByRegistration(Registration registration);

  @Query("SELECT coalesce(SUM(p.amount), 0) FROM RegistrationPayment p  WHERE p.amount is not null and p.registration = :registration")
  Long sumAmountByRegistration(Registration registration);
}

