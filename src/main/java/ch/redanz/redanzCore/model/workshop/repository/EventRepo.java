package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
  Event findByName(String name);

  List<Event> findAllByActiveAndArchived(Boolean active, Boolean archived);
  List<Event> findAllByArchived(Boolean archived);

  Event findByEventId(Long eventId);

}
