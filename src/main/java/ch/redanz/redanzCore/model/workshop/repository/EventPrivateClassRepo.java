package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventPrivateClass;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventPrivateClassRepo extends JpaRepository<EventPrivateClass, Long> {
  EventPrivateClass findByEventAndPrivateClass(Event event, PrivateClass privateClass);
  List<EventPrivateClass> findByEvent(Event event);
  EventPrivateClass findByEventPrivateClassId(Long eventPrivateClassId);
}
