package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum BundleTrackConfig {
  LEVEL_ADVANCED(BundleConfig.FULL_PASS, TrackConfig.ADVANCED),
  LEVEL_INTERMEDIATE(BundleConfig.FULL_PASS, TrackConfig.INTERMEDIATE),
  LEVEL_NO_LEVEL(BundleConfig.FULL_PASS, TrackConfig.NO_LEVEL);

  private final BundleConfig bundle;
  private final TrackConfig track;

  BundleTrackConfig(BundleConfig bundle, TrackConfig track) {
    this.bundle = bundle;
    this.track = track;
  }

  public static void setup(BundleService bundleService, TrackService trackService) {
    for (BundleTrackConfig bundleTrackConfig : BundleTrackConfig.values()) {
      if (!bundleService.bundleTrackExists(bundleService.findByName(bundleTrackConfig.getBundle().getName()), trackService.findByName(bundleTrackConfig.getTrack().getName())))
      {
        bundleService.save(
          new BundleTrack(
            trackService.findByName(bundleTrackConfig.getTrack().getName()),
            bundleService.findByName(bundleTrackConfig.getBundle().getName())
          )
        );
      }
    }
  }
}
