package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.BundleTrack;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
public enum BundleTrackConfig {
  LEVEL_ADVANCED(BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED),
  LEVEL_INTERMEDIATE(BundleConfig.LEVELPASS, TrackConfig.LINDY_INTERMEDIATE),
  LEVEL_BEGINNER(BundleConfig.LEVELPASS, TrackConfig.LINDY_BEGINNER),
  FULL_FUN_TRACK(BundleConfig.FULLPASS, TrackConfig.FUN_TRACK),
  HALF_FUN_TRACK(BundleConfig.HALFPASS, TrackConfig.FUN_TRACK);

//  HALF_ADVANCED(BundleConfig.HALFPASS, TrackConfig.LINDY_ADVANCED),
//  HALF_INTERMEDIATE(BundleConfig.HALFPASS, TrackConfig.LINDY_INTERMEDIATE),
//  HALF_SOLOJAZZ(BundleConfig.HALFPASS, TrackConfig.SOLOJAZZ);

  private final BundleConfig bundle;
  private final TrackConfig track;

  BundleTrackConfig(BundleConfig bundle, TrackConfig track) {
    this.bundle = bundle;
    this.track = track;
  }

  public static List<BundleTrack> setup(TrackRepo trackRepo, BundleRepo bundleRepo) {
    List<BundleTrack> transitions = new ArrayList<>();

    for (BundleTrackConfig bundleTrackConfig : BundleTrackConfig.values()) {
      transitions.add(
        new BundleTrack(
          trackRepo.findByName(bundleTrackConfig.getTrack().getName()),
          bundleRepo.findByName(bundleTrackConfig.getBundle().getName())
        )
      );
    }
    return transitions;
  }
}
