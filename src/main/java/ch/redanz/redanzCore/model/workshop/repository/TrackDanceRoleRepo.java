package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackDanceRoleRepo extends JpaRepository<TrackDanceRole, Long> {
  TrackDanceRole findByTrackAndDanceRole(Track track, DanceRole danceRole);
  TrackDanceRole findByTrackDanceRoleId(Long trackDanceRoleId);
  boolean existsByTrackAndDanceRole(Track track, DanceRole danceRole);
}
