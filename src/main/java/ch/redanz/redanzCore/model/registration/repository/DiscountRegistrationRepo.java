package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountRegistrationRepo extends JpaRepository<DiscountRegistration, Long> {
  List<DiscountRegistration> findAllByRegistration(Registration registration);
//  List<DiscountRegistration> findAllByRegistrationAndDiscountCapacity(Registration registration, Double discount);
  Optional<DiscountRegistration> findAllByRegistrationAndDiscount(Registration registration, Discount discount);
//  void deleteAllByRegistration(Registration registration);
  void deleteAllByRegistrationAndDiscount(Registration registration, Discount discount);
  int countAllByDiscountAndRegistrationEvent(Discount discount, Event event);
  int countAllByDiscountAndRegistrationWorkflowStatusAndRegistrationEvent(Discount discount, WorkflowStatus workflowStatus, Event event);
  boolean existsByRegistrationAndDiscount(Registration registration, Discount discount);
}
