package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
  Event findByName(String name);

  List<Event> findAllByActiveAndArchived(Boolean active, Boolean archived);
  List<Event> findAllByArchived(Boolean archived);
  Event findDistinctFirstByActive(Boolean active);
  Event findByEventId(Long eventId);
  Boolean existsByName(String name);
//  List<Special> findSpecialsByEvent(Event event);
//  List<Special> findSpecialsByEvent(Event event);

}
