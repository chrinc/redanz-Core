package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivateClassRepo extends  JpaRepository<PrivateClass, Long> {
  PrivateClass findByPrivateClassId(Long privateClassId);


  @Query("SELECT s FROM Event e JOIN e.privateClasses s WHERE e = :event")
  List<PrivateClass> findAllByEvent(@Param("event") Event event);


}
