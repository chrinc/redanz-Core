package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventDanceRoleRepo extends JpaRepository<EventDanceRole, Long> {
  EventDanceRole findByEventAndDanceRole(Event event, DanceRole danceRole);
  EventDanceRole findByEventDanceRoleId(Long eventDanceRole);
  boolean existsByEventAndDanceRole(Event event, DanceRole danceRole);

}
