package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.entities.BundleTrackId;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackBundleRepo extends JpaRepository<BundleTrack, BundleTrackId> {

  List<BundleTrack> findAllByBundle(Bundle bundle);
  boolean existsByBundleAndTrack(Bundle bundle, Track track);
  BundleTrack findByBundleAndTrack(Bundle bundle, Track track);
}
