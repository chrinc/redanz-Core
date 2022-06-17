package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.Slot;
import ch.redanz.redanzCore.model.workshop.TypeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TypeSlotRepo extends JpaRepository<TypeSlot, Long> {
  List<TypeSlot> findAllByType(String type);
}
