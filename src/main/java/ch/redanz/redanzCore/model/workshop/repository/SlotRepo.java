package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlotRepo extends JpaRepository<Slot, Long> {
  Slot findByName(String name);
  boolean existsByName(String name);

  Slot findBySlotId(Long slotId);
}
