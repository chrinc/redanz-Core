package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.FoodRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialRegistrationRepo extends JpaRepository<SpecialRegistration, Long> {
  List<SpecialRegistration> findAllByRegistration(Registration registration);
  void deleteAllByRegistrationAndSpecialId(Registration registration, Special special);
  int countAllBySpecialIdAndRegistration_WorkflowStatus(Special special, WorkflowStatus workflowStatus);
}
