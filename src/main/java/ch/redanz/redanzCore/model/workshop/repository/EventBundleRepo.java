package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventBundleRepo extends JpaRepository<EventBundle, Long> {
  List<EventBundle> findAllByEvent(Event event);
  Boolean existsByEventAndBundle(Event event, Bundle bundle);
  EventBundle findByEventAndBundle(Event event, Bundle bundle);
//  void deleteByEventBundleId(Long eventBundleId);
//
//  @Query("DELETE FROM EventBundle e WHERE e.eventBundleId = :eventBundleId")
//  void deleteBy(@Param("eventBundleId") Long eventBundleId);
}
