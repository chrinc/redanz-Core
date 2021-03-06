package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialRegistrationRepo extends JpaRepository<SpecialRegistration, Long> {
  List<SpecialRegistration> findAllByRegistration(Registration registration);
}
