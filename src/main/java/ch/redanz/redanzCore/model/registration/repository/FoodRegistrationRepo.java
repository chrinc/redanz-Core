package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.FoodRegistration;
import ch.redanz.redanzCore.model.registration.FoodRegistrationId;
import ch.redanz.redanzCore.model.registration.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRegistrationRepo extends JpaRepository<FoodRegistration, FoodRegistrationId> {
List<FoodRegistration> findAllByFoodRegistrationIdRegistrationId(Long registrationId);
}
