package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRegistrationRepo extends JpaRepository<FoodRegistration, Long> {
List<FoodRegistration> findAllByRegistration(Registration registration);

int countAllByFoodAndRegistrationWorkflowStatusAndRegistrationEvent(
  Food food, WorkflowStatus workflowStatus, Event event
);
}
