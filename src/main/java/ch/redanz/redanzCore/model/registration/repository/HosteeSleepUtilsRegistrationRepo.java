package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.entities.HosteeSleepUtilRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HosteeSleepUtilsRegistrationRepo extends JpaRepository<HosteeSleepUtilRegistration, Long> {
  List<HosteeSleepUtilRegistration> findAllByHosteeRegistration(HosteeRegistration hosteeRegistration);

}
