package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventSpecial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialRegistrationRepo extends JpaRepository<SpecialRegistration, Long> {
  List<SpecialRegistration> findAllByRegistration(Registration registration);
  List<SpecialRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  List<SpecialRegistration> findAllByEventSpecialAndRegistrationActive(Event event, Boolean active);
  Boolean existsByEventSpecialAndRegistrationActive(EventSpecial eventSpecial, Boolean active);

  void deleteAllByRegistrationAndEventSpecial(Registration registration, EventSpecial eventSpecial);
  int countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEvent(EventSpecial eventSpecial, WorkflowStatus workflowStatus, Event event);
  int countAllByEventSpecialAndRegistration_WorkflowStatusAndRegistrationEventAndRegistrationDanceRole(EventSpecial eventSpecial, WorkflowStatus workflowStatus, Event event, DanceRole danceRole);
}
