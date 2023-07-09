package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecialRegistrationRepo extends JpaRepository<SpecialRegistration, Long> {
  List<SpecialRegistration> findAllByRegistration(Registration registration);
  List<SpecialRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);

  void deleteAllByRegistrationAndSpecialId(Registration registration, Special special);
  int countAllBySpecialIdAndRegistration_WorkflowStatusAndRegistrationEvent(Special special, WorkflowStatus workflowStatus, Event event);
}
