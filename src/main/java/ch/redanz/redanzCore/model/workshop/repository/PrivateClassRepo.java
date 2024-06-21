package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.Optional;

public interface PrivateClassRepo extends  JpaRepository<PrivateClass, Long> {
  PrivateClass findByPrivateClassId(Long privateClassId);
  PrivateClass findByName(String name);
  boolean existsByName(String name);


//  @Query("SELECT s FROM Event e JOIN e.privateClasses s WHERE e = :event")
//  Optional<List<PrivateClass>> findAllByEvent(@Param("event") Event event);

//  @Override
//  @Modifying
//  @Query("delete from PrivateClass t where t.privateClassId = ?1")
//  void deleteById(Long aLong);
}
