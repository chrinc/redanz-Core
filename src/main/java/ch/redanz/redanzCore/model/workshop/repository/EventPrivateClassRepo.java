package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventPrivateClass;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventPrivateClassRepo extends JpaRepository<EventPrivateClass, Long> {
  List<EventPrivateClass> findAllByEvent(Event event);
  Boolean existsByName(String name);
  EventPrivateClass findByName(String name);
  EventPrivateClass findByEventPrivateClassId(Long eventPrivateClassId);
}
