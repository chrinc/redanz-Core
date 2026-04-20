package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerSlotRegistration;
import ch.redanz.redanzCore.model.workshop.entities.EventSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerSlotRegistrationRepo extends JpaRepository<VolunteerSlotRegistration, Long> {
  List<VolunteerSlotRegistration> findAllByVolunteerRegistration(VolunteerRegistration volunteerRegistration);
  void deleteAllByVolunteerRegistrationAndEventSlot(VolunteerRegistration volunteerRegistration, EventSlot eventSlot);
  void deleteAllByVolunteerRegistration(VolunteerRegistration volunteerRegistration);
  boolean existsByEventSlotAndVolunteerRegistrationRegistrationActive(EventSlot eventSlot, Boolean active);
}
