package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VolunteerTypeRepo  extends JpaRepository<VolunteerType, Long> {
  VolunteerType findByVolunteerTypeId(Long volunteerTypeId);
  VolunteerType findByName(String name);
  boolean existsByName(String name);

  @Query("SELECT s FROM Event e JOIN e.volunteerTypes s WHERE e = :event")
  List<VolunteerType> findAllByEvent(@Param("event") Event event);
}
