package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerSlotRegistration;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerSlotRegistrationRepo extends JpaRepository<VolunteerSlotRegistration, Long> {
  List<VolunteerSlotRegistration> findAllByVolunteerRegistration(VolunteerRegistration volunteerRegistration);
  void deleteAllByVolunteerRegistrationAndSlot(VolunteerRegistration volunteerRegistration, Slot slot);
  void deleteAllByVolunteerRegistration(VolunteerRegistration volunteerRegistration);
//  void deleteAllByRegistration(Registration registration);
}
