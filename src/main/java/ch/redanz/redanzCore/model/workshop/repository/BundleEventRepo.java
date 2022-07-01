package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.BundleTrackId;
import ch.redanz.redanzCore.model.workshop.entities.EventBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleEventRepo extends JpaRepository<EventBundle, BundleTrackId> {
}
