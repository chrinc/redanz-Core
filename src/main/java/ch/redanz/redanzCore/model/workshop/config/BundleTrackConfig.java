package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum BundleTrackConfig {
  LEVEL_ADVANCED(BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED),
  LEVEL_INTERMEDIATE(BundleConfig.LEVELPASS, TrackConfig.LINDY_INTERMEDIATE),
  LEVEL_BEGINNER(BundleConfig.LEVELPASS, TrackConfig.LINDY_BEGINNER),
  FULL_FUN_TRACK(BundleConfig.FULLPASS, TrackConfig.FUN_TRACK),
  HALF_FUN_TRACK(BundleConfig.HALFPASS, TrackConfig.FUN_TRACK);

  private final BundleConfig bundle;
  private final TrackConfig track;

  BundleTrackConfig(BundleConfig bundle, TrackConfig track) {
    this.bundle = bundle;
    this.track = track;
  }

  public static void setup(BundleService bundleService, TrackService trackService) {
    for (BundleTrackConfig bundleTrackConfig : BundleTrackConfig.values()) {
      bundleService.save(
        new BundleTrack(
          trackService.findByName(bundleTrackConfig.getTrack().getName()),
          bundleService.findByName(bundleTrackConfig.getBundle().getName())
        )
      );
    }
  }
}
