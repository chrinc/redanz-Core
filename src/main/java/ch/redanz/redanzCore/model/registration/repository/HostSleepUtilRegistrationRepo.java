package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HostRegistration;
import ch.redanz.redanzCore.model.registration.entities.HostSleepUtilRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostSleepUtilRegistrationRepo extends JpaRepository<HostSleepUtilRegistration, Long> {
    List<HostSleepUtilRegistration> findAllByHostRegistration(HostRegistration hostRegistration);
    void deleteAllByHostRegistration(HostRegistration hostRegistration);
    void deleteAllByHostRegistrationAndSleepUtil(HostRegistration hostRegistration, SleepUtil sleepUtil);
    HostSleepUtilRegistration findAllByHostRegistrationAndSleepUtil(HostRegistration hostRegistration, SleepUtil sleepUtil);
//    void deleteAllByRegistration(Registration registration);
}
