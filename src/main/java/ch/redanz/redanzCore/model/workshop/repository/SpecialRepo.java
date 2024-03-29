package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.entities.TrackDiscountId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SpecialRepo extends JpaRepository<Special, Long> {
  Special findByName(String name);
  Special findBySpecialId(Long name);
  boolean existsByName(String name);


  @Query("SELECT s FROM Event e JOIN e.specials s WHERE e = :event")
  Optional<Set<Special>> findAllByEvent(@Param("event") Event event);

  @Query("SELECT s FROM Bundle b JOIN b.specials s WHERE b = :bundle")
  Optional<Set<Special>> findAllByBundle(@Param("bundle") Bundle bundle);

  @Override
  @Modifying
  @Query("delete from Special t where t.specialId = ?1")
  void deleteById(Long aLong);
}
