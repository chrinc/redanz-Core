package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.entities.BundleTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackBundleRepo extends JpaRepository<BundleTrack, BundleTrackId> {
}
