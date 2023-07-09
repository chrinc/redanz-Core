package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrivateClassRegistrationRepo extends JpaRepository<PrivateClassRegistration, Long> {
  List<PrivateClassRegistration> findAllByRegistration(Registration registration);
  List<PrivateClassRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  void deleteAllByRegistrationAndPrivateClass(Registration registration, PrivateClass privateClass);
  int countAllByPrivateClassAndRegistration_WorkflowStatus(PrivateClass privateClass, WorkflowStatus workflowStatus);
}
