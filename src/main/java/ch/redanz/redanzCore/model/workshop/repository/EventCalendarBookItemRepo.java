package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendar;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendarBookItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface EventCalendarBookItemRepo extends JpaRepository<EventCalendarBookItem, Long> {
  List<EventCalendarBookItem> findByEventCalendar(EventCalendar eventCalendar);
  Boolean existsByEventCalendarEventAndBookItemIdAndBookItemType(Event event, Long bookItemId, BookItemType bookItemType);
  List<EventCalendarBookItem> findAllByEventCalendarEventAndBookItemIdAndBookItemType(Event event, Long bookItemId, BookItemType bookItemType);

  void deleteByEventCalendar(EventCalendar eventCalendar);
}
