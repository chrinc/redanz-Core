package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HostRegistration;
import ch.redanz.redanzCore.model.registration.HostSleepUtilRegistration;
import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.HosteeSleepUtilRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeSleepUtilsRegistrationRepository  extends JpaRepository<HosteeSleepUtilRegistration, Long> {
  List<HosteeSleepUtilRegistration> findAllByHosteeRegistration(HosteeRegistration hosteeRegistration);

}
