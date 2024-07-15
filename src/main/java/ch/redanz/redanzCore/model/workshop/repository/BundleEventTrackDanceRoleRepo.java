package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BundleEventTrackDanceRoleRepo extends JpaRepository<BundleEventTrackDanceRole, Long> {
  BundleEventTrackDanceRole findByBundleEventTrackAndEventDanceRole(BundleEventTrack bundleEventTrack, EventDanceRole eventDanceRole);
  BundleEventTrackDanceRole findByBundleEventTrackDanceRoleId(Long bundleEventTrackDanceRoleId);
  boolean existsByBundleEventTrackAndEventDanceRole(BundleEventTrack bundleEventTrack, EventDanceRole eventDanceRole);
  void deleteAllByBundleEventTrack(BundleEventTrack bundleEventTrack);
}
