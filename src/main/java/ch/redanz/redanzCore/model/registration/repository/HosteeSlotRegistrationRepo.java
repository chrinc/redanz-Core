package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.HosteeSlotRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeSlotRegistrationRepo extends JpaRepository<HosteeSlotRegistration, Long> {
  List<HosteeSlotRegistration> findAllByHosteeRegistration(HosteeRegistration hosteeRegistration);
}
