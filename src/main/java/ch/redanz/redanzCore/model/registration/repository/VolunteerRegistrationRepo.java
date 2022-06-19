package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.HosteeSlotRegistration;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.VolunteerRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerRegistrationRepo extends JpaRepository<VolunteerRegistration, Long> {
  VolunteerRegistration findByRegistration (Registration registration);
}
