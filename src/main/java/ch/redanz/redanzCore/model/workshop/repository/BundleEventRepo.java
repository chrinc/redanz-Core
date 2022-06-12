package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.EventBundle;
import ch.redanz.redanzCore.model.workshop.BundleTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleEventRepo extends JpaRepository<EventBundle, BundleTrackId> {
}
