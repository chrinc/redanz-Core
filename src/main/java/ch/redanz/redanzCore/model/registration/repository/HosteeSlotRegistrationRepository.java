package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HostRegistration;
import ch.redanz.redanzCore.model.registration.HostSlotRegistration;
import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.HosteeSlotRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeSlotRegistrationRepository extends JpaRepository<HosteeSlotRegistration, Long> {
  List<HosteeSlotRegistration> findAllByHosteeRegistration(HosteeRegistration hosteeRegistration);
}
