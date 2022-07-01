package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiscountRegistrationRepo extends JpaRepository<DiscountRegistration, Long> {
  List<DiscountRegistration> findAllByRegistration(Registration registration);
}
