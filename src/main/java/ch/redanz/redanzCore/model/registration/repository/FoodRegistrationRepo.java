package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.FoodRegistrationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRegistrationRepo extends JpaRepository<FoodRegistration, FoodRegistrationId> {
List<FoodRegistration> findAllByFoodRegistrationIdRegistrationId(Long registrationId);
}
