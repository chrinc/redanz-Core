package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum BundleTrackConfig {
//  LEVEL_ADVANCED(BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED),
//  LEVEL_INTERMEDIATE(BundleConfig.LEVELPASS, TrackConfig.LINDY_INTERMEDIATE),
//  LEVEL_BEGINNER(BundleConfig.LEVELPASS, TrackConfig.LINDY_BEGINNER),
  EXTRA_FUN_TRACK(BundleConfig.EXTRA_FUN_PASS, TrackConfig.FUN_FREE_CHOICE),
  FUN_TRACK(BundleConfig.FUN_PASS, TrackConfig.FUN_FREE_CHOICE);

  private final BundleConfig bundle;
  private final TrackConfig track;

  BundleTrackConfig(BundleConfig bundle, TrackConfig track) {
    this.bundle = bundle;
    this.track = track;
  }

  public static void setup(BundleService bundleService, TrackService trackService) {
    for (BundleTrackConfig bundleTrackConfig : BundleTrackConfig.values()) {
      if (!bundleService.bundleTrackExists(bundleService.findByInternalId(bundleTrackConfig.getBundle().getInternalId()), trackService.findByInternalId(bundleTrackConfig.getTrack().getInternalId())))
      {
        bundleService.save(
          new BundleTrack(
            trackService.findByInternalId(bundleTrackConfig.getTrack().getInternalId()),
            bundleService.findByInternalId(bundleTrackConfig.getBundle().getInternalId())
          )
        );
      }
    }
  }
}
