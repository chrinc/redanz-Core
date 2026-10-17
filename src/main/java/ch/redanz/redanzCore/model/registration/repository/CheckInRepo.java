package ch.redanz.redanzCore.model.registration.repository;


import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckInRepo extends JpaRepository<CheckIn, Long> {
  List<CheckIn> findAllByEvent(Event event);
  void deleteAllByEvent(Event event);

  // one statement instead of loading and deleting each check-in
  @Modifying(flushAutomatically = true)
  @Query("delete from CheckIn c where c.event = :event")
  void bulkDeleteByEvent(@Param("event") Event event);
  CheckIn findByCheckInId(Long checkInId);
  boolean existsByGuest(Guest guest);
  CheckIn findByGuest(Guest guest);
  boolean existsByRegistration(Registration registration);
  CheckIn findByRegistration(Registration registration);
  void deleteByCheckInId(Long checkInId);
}
