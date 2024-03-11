package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.TrackBundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackDiscountRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class TrackService {

  private final TrackRepo trackRepo;
  private final TrackBundleRepo trackBundleRepo;
  private final TrackDanceRoleRepo trackDanceRoleRepo;
  private final TrackDiscountRepo trackDiscountRepo;
  private final BundleService bundleService;
  public boolean trackDiscountExists(Track track, Discount discount) {
    return trackDiscountRepo.existsByTrackAndDiscount(track, discount);
  }
  public void save(Track track) {
    trackRepo.save(track);
  }

  public void save(TrackDanceRole trackDanceRole) {
    trackDanceRoleRepo.save(trackDanceRole);
  }

  public void save(TrackDiscount trackDiscount) {
    trackDiscountRepo.save(trackDiscount);
  }

  public boolean existsByTrackDanceRole(Track track, DanceRole danceRole) {
    return trackDanceRoleRepo.existsByTrackAndDanceRole(track, danceRole);
  }
  public List<Track> getAll(){
    return trackRepo.findAll();
  }
  public Set<Track> getAllByEvent(Event event){
    List<Bundle> bundles = bundleService.getAllByEvent(event);
    Set<Track> tracks = new HashSet<>();

    bundles.forEach(bundle -> {
      bundle.getBundleTracks().forEach(bundleTrack -> {
        tracks.add(bundleTrack.getTrack());
      });
    });

    return tracks;
  }

  public boolean bundleHasTrack(Bundle bundle) {
    return !trackBundleRepo.findAllByBundle(bundle).isEmpty();
  }

  public boolean existsByName(String TrackName) {
    return trackRepo.existsByName(TrackName);
  }

  public Track findByTrackId(Long trackId) {
    return trackRepo.findByTrackId(trackId);
  }

  public Track findByName(String name) {
    return trackRepo.findByName(name);
  }
  public Track findByInternalId(String internalId) {
    return trackRepo.findByInternalId(internalId);
  }
}
