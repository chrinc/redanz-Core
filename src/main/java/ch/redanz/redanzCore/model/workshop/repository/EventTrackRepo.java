package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTrackRepo extends JpaRepository<EventTrack, Long> {
  EventTrack findByEventAndTrack(Event event, Track track);
  EventTrack findByEventTrackId(Long eventTrackId);
  boolean existsByEventAndTrack(Event event, Track track);
}
