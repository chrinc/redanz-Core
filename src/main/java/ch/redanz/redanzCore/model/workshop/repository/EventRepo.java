package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRepo extends JpaRepository<Event, Long> {
  Event findByName(String name);

  List<Event> findAllByActiveAndArchived(Boolean active, Boolean archived);
  List<Event> findAllByArchivedOrderByEventIdDesc(Boolean archived);
  Event findDistinctFirstByActive(Boolean active);
  Event findByEventId(Long eventId);
  Boolean existsByName(String name);

  @Query("SELECT e.capacity FROM Event e WHERE e.eventId = :eventId")
  Integer findCapacityByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.soldOut FROM Event e WHERE e.eventId = :eventId")
  boolean findSoldOutByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.eventBundles FROM Event e WHERE e.eventId = :eventId")
  Set<EventBundle> findAllEventBundlesByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.eventTracks FROM Event e WHERE e.eventId = :eventId")
  Set<EventTrack> findAllEventTracksByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.eventSpecials FROM Event e WHERE e.eventId = :eventId")
  Set<EventSpecial> findAllEventSpecialsByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.eventPrivates FROM Event e WHERE e.eventId = :eventId")
  Set<EventPrivateClass> findAllEventPrivatesByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.name FROM Event e WHERE e.eventId = :eventId")
  String findEventNameByEventId(@Param("eventId") Long eventId);

  @Query("SELECT e.active FROM Event e WHERE e.eventId = :eventId")
  boolean findIsActiveByEventId(@Param("eventId") Long eventId);

}
