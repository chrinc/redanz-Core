package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface FoodRegistrationRepo extends JpaRepository<FoodRegistration, Long> {
void deleteAllByRegistration(Registration registration);
void deleteAllByRegistrationAndFoodAndSlot(Registration registration, Food food, Slot slot);

List<FoodRegistration> findAllByRegistration(Registration registration);
List<FoodRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);

int countAllByFoodAndRegistrationWorkflowStatusAndRegistrationEvent(
  Food food, WorkflowStatus workflowStatus, Event event
);

int countAllByFoodAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
  Food food, Slot slot, WorkflowStatus workflowStatus, Event event
);
}
