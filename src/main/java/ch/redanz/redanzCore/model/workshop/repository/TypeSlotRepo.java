package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeSlotRepo extends JpaRepository<TypeSlot, Long> {
  List<TypeSlot> findAllByType(String type);
  TypeSlot findByTypeAndSlot(String type, Slot slot);
  TypeSlot findByTypeAndTypeObjectIdAndSlot(String type, Long typeObjectId, Slot slot);
  boolean existsByTypeAndTypeObjectIdAndSlot(String type, Long typeObjectId, Slot slot);
  TypeSlot findByTypeSlotId(Long typeSlotId);

  @Override
  @Modifying
  @Query("delete from TypeSlot t where t.typeSlotId = ?1")
  void deleteById(Long typeSlotId);
}
