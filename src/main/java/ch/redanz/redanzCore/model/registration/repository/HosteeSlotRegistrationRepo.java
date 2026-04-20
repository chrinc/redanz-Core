package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.HosteeSlotRegistration;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeSlotRegistrationRepo extends JpaRepository<HosteeSlotRegistration, Long> {
  List<HosteeSlotRegistration> findAllByHosteeRegistration(HosteeRegistration hosteeRegistration);
  void deleteAllByHosteeRegistrationAndEventSlot(HosteeRegistration hosteeRegistration, EventSlot eventSlot);
  void deleteAllByHosteeRegistration(HosteeRegistration hosteeRegistration);

  boolean existsByEventSlotAndHosteeRegistrationRegistrationActive(EventSlot eventSlot, Boolean active);
}
