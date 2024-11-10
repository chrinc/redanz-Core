package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BundleEventTrackRepo extends JpaRepository<BundleEventTrack, Long> {
  BundleEventTrack findByBundleAndEventTrack(Bundle bundle, EventTrack eventTrack);
  BundleEventTrack findByBundleEventTrackId(Long bundleEventTrackId);
  BundleEventTrack findByEventTrackEventAndBundleAndEventTrackTrack(Event event, Bundle bundle, Track track);
  boolean existsByBundleAndEventTrack(Bundle bundle, EventTrack eventTrack);
  List<BundleEventTrack> findAllByEventTrack(EventTrack eventTrack);
  List<BundleEventTrack> findAllByBundleAndEventTrackEvent(Bundle bundle, Event event);
}
