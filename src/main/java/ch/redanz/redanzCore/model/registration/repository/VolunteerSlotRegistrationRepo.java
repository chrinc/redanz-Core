package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.HosteeRegistration;
import ch.redanz.redanzCore.model.registration.HosteeSlotRegistration;
import ch.redanz.redanzCore.model.registration.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.VolunteerSlotRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerSlotRegistrationRepo extends JpaRepository<VolunteerSlotRegistration, Long> {
  List<VolunteerSlotRegistration> findAllByVolunteerRegistration(VolunteerRegistration volunteerRegistration);

}
