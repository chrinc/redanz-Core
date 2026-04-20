package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventSpecialRepo extends JpaRepository<EventSpecial, Long> {
  EventSpecial findByEventSpecialId(Long eventSpecialId);
  EventSpecial findByName(String name);
  List<EventSpecial> findAllByEvent(Event event);
  List<EventSpecial> findAllByEventAndInfoOnly(Event event, Boolean infoOnly);
  Boolean existsByName(String name);
}
