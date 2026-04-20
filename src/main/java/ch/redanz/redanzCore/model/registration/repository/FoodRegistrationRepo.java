package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventFoodSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRegistrationRepo extends JpaRepository<FoodRegistration, Long> {
void deleteAllByRegistration(Registration registration);

void deleteAllByRegistrationAndEventFoodSlot(Registration registration, EventFoodSlot eventFoodSlot);

List<FoodRegistration> findAllByRegistration(Registration registration);
List<FoodRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
List<FoodRegistration> findAllByRegistrationActive(Boolean active);

int countAllByEventFoodSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
  EventFoodSlot eventFoodSlot, WorkflowStatus workflowStatus, Event event
);

//int countAllByEventFoodSlotAndAndSlotAndRegistrationWorkflowStatusAndRegistrationEvent(
//  EventFoodSlot eventFoodSlot, Slot slot, WorkflowStatus workflowStatus, Event event
//);
}
