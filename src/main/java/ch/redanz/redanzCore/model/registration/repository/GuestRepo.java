package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuestRepo  extends JpaRepository<Guest, Long> {
  List<Guest> findAllByEventAndActive(Event event, boolean active);
  List<Guest> findAllByEventAndActiveAndSlotsContains(Event event, boolean active, Slot slot);
 }
