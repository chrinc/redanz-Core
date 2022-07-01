package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.entities.HostSleepUtilRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostSleepUtilRegistrationRepo extends JpaRepository<HostSleepUtilRegistration, Long> {
    List<HostSleepUtilRegistration> findAllByHostRegistration(HostRegistration hostRegistration);
}
