package ch.redanz.redanzCore.model.registration.repository;


import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CheckInRepo extends JpaRepository<CheckIn, Long> {
  List<CheckIn> findAllByEvent(Event event);
  void deleteAllByEvent(Event event);
  CheckIn findByCheckInId(Long checkInId);
}
