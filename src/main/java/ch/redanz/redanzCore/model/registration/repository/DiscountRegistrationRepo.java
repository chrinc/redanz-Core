package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.DiscountRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountRegistrationRepo extends JpaRepository<DiscountRegistration, Long> {
  List<DiscountRegistration> findAllByRegistration(Registration registration);
  Optional<DiscountRegistration> findAllByRegistrationAndEventDiscount(Registration registration, EventDiscount eventDiscount);
  void deleteAllByRegistrationAndEventDiscount(Registration registration, EventDiscount eventDiscount);
  int countAllByEventDiscount(EventDiscount eventDiscount);
  int countAllByEventDiscountAndRegistrationWorkflowStatus(EventDiscount eventDiscount, WorkflowStatus workflowStatus);
  boolean existsByRegistrationAndEventDiscount(Registration registration, EventDiscount eventDiscount);
  List<DiscountRegistration> findAllByRegistrationEventAndRegistrationActive(Event event, Boolean active);
  boolean existsByEventDiscountAndRegistrationActive(EventDiscount eventDiscount, Boolean active);
}
