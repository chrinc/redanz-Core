package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TrackRepo extends JpaRepository<Track, Long> {
  Track findByName(String name);
  Track findByTrackId(Long trackId);

}
