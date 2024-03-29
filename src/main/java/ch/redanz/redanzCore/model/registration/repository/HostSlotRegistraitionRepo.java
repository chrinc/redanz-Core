package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.entities.HostSlotRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostSlotRegistraitionRepo extends JpaRepository<HostSlotRegistration, Long> {
    List<HostSlotRegistration> findAllByHostRegistration(HostRegistration hostRegistration);
    void deleteAllByHostRegistration(HostRegistration hostRegistration);
    void deleteAllByHostRegistrationAndSlot(HostRegistration hostRegistration, Slot slot);
}
