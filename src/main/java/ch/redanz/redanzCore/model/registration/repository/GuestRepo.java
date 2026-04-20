package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepo  extends JpaRepository<Guest, Long> {

  @Query("SELECT g FROM Guest g WHERE g.event =:event and g.active = :active")
  List<Guest> findAllByEventAndActive(@Param("event") Event event, @Param("active") boolean active);
//  List<Guest> findAllByEventAndActiveAndSlotsContains(Event event, boolean active, EventSlot eventSlot);
 }
