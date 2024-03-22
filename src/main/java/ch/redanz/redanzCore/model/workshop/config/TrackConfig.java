package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.DanceLevel;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackConfig {
  ADVANCED("Advanced Track", "", 998, true, null),
  INTERMEDIATE("Intermediate Track", "", 998, true, null),
  NO_LEVEL("No Level Track", "", 998, true, null);

  private final String name;
  private final String description;
  private final Integer capacity;
  private final Boolean partnerRequired;
  private final DanceLevel requiredDanceLevel;

  public static void setup(TrackService trackService) {
    for (TrackConfig trackConfig : TrackConfig.values()) {
      if (!trackService.existsByName(trackConfig.name)) {
        trackService.save(
          new Track(
            trackConfig.name,
            trackConfig.description,
            trackConfig.capacity,
            trackConfig.partnerRequired,
            trackConfig.requiredDanceLevel
          )
        );
      } else {
        Track track = trackService.findByName(trackConfig.name);
        track.setDescription(trackConfig.description);
        track.setCapacity(trackConfig.capacity);
        track.setPartnerRequired(trackConfig.partnerRequired);
        track.setRequiredDanceLevel(trackConfig.requiredDanceLevel);
        trackService.save(track);
      }
    }
  }
}
