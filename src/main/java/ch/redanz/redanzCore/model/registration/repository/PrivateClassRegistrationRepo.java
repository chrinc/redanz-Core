package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventPrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateClassRegistrationRepo extends JpaRepository<PrivateClassRegistration, Long> {
  List<PrivateClassRegistration> findAllByRegistration(Registration registration);
  List<PrivateClassRegistration> findAllByRegistrationActive(Boolean active);
  List<PrivateClassRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  Boolean existsByEventPrivateClassAndRegistrationActive(EventPrivateClass eventPrivateClass, Boolean active);
  void deleteAllByRegistrationAndEventPrivateClass(Registration registration, EventPrivateClass eventPrivateClass);
  int countAllByEventPrivateClassAndRegistration_WorkflowStatus(EventPrivateClass eventPrivateClass, WorkflowStatus workflowStatus);
  int countAllByEventPrivateClassAndRegistration_WorkflowStatusAndRegistrationDanceRole(EventPrivateClass eventPrivateClass, WorkflowStatus workflowStatus, DanceRole danceRole);

}
