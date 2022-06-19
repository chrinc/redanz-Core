package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HostRegistration;
import ch.redanz.redanzCore.model.registration.HostSlotRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostSlotRegistraitionRepository extends JpaRepository<HostSlotRegistration, Long> {
    List<HostSlotRegistration> findAllByHostRegistration(HostRegistration hostRegistration);
}
