package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventCalendarRepo extends JpaRepository<EventCalendar, Long> {
  List<EventCalendar> findAllByEvent(Event event);
  EventCalendar findByTitle(String title);
}
