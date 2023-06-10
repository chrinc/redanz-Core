package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationMatchingRepo extends JpaRepository<RegistrationMatching, Long> {

  Optional<RegistrationMatching> findByRegistration1(Registration registration1);
  Optional<RegistrationMatching> findByRegistration2(Registration registration2);

  boolean existsByRegistration2(Registration registration2);
  void deleteAllByRegistration1(Registration registration);
  List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull();

  List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(WorkflowStatus workflowStatus, Event event);
}
