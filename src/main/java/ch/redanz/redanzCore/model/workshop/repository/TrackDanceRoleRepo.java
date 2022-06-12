package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.TrackDanceRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackDanceRoleRepo extends JpaRepository<TrackDanceRole, TrackDanceRoleId> {
}