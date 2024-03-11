package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.entities.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.entities.TrackDanceRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackDanceRoleRepo extends JpaRepository<TrackDanceRole, TrackDanceRoleId> {
  boolean existsByTrackAndDanceRole(Track track, DanceRole danceRole);
}
