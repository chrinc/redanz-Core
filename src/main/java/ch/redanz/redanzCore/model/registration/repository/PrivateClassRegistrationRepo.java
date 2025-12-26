package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateClassRegistrationRepo extends JpaRepository<PrivateClassRegistration, Long> {
  List<PrivateClassRegistration> findAllByRegistration(Registration registration);
  List<PrivateClassRegistration> findAllByRegistrationActive(Boolean active);
  List<PrivateClassRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  void deleteAllByRegistrationAndPrivateClass(Registration registration, PrivateClass privateClass);
  int countAllByRegistration_EventAndPrivateClassAndRegistration_WorkflowStatus(Event event, PrivateClass privateClass, WorkflowStatus workflowStatus);
  int countAllByPrivateClassAndRegistration_WorkflowStatusAndRegistrationEvent(PrivateClass privateClass, WorkflowStatus workflowStatus, Event event);
  int countAllByPrivateClassAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(PrivateClass privateClass, WorkflowStatus workflowStatus, Event event, DanceRole danceRole);

}
