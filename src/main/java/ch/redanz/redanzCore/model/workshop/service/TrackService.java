package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.entities.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.entities.TrackDiscount;
import ch.redanz.redanzCore.model.workshop.repository.TrackDanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrackService {

  private final TrackRepo trackRepo;
  private final TrackDanceRoleRepo trackDanceRoleRepo;
  private final TrackDiscountRepo trackDiscountRepo;

  public void save(Track track) {
    trackRepo.save(track);
  }

  public void save(TrackDanceRole trackDanceRole) {
    trackDanceRoleRepo.save(trackDanceRole);
  }

  public void save(TrackDiscount trackDiscount) {
    trackDiscountRepo.save(trackDiscount);
  }


  public Track findByTrackId(Long trackId) {
    return trackRepo.findByTrackId(trackId);
  }

  public Track findByName(String name) {
    return trackRepo.findByName(name);
  }
}
