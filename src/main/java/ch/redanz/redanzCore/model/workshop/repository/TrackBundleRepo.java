package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.BundleTrack;
import ch.redanz.redanzCore.model.workshop.BundleTrackId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackBundleRepo extends JpaRepository<BundleTrack, BundleTrackId> {
}
