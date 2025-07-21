package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventSpecialRepo extends JpaRepository<EventSpecial, Long> {
  EventSpecial findByEventAndSpecial(Event event, Special special);
  EventSpecial findByEventSpecialId(Long eventSpecialId);
  List<EventSpecial> findAllByEvent(Event event);
  List<EventSpecial> findAllByEventAndInfoOnly(Event event, Boolean infoOnly);
  boolean existsByEventAndSpecial(Event event, Special special);
}
