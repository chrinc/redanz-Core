package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.BundleTrackId;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BundleEventRepo extends JpaRepository<EventBundle, BundleTrackId> {
  List<EventBundle> findAllByEvent(Event event);
  Boolean existsByEventAndBundle(Event event, Bundle bundle);
  EventBundle findByEventAndBundle(Event event, Bundle bundle);
}
