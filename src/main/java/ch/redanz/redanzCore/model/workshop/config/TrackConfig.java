package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.DanceLevel;
import ch.redanz.redanzCore.model.workshop.entities.Track;
import ch.redanz.redanzCore.model.workshop.repository.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackConfig {
  BASIC_LEVEL_TRACK("Basic Level", "", 998, true, null, "basic_level"),
  ADVANCED_LEVEL_TRACK("Advanced Level", "", 998, true, null, "advanced_level");

  private final String name;
  private final String description;
  private final Integer capacity;
  private final Boolean partnerRequired;
  private final DanceLevel requiredDanceLevel;
  private final String internalId;

  public static void setup(TrackService trackService) {
    for (TrackConfig trackConfig : TrackConfig.values()) {
      if (!trackService.existsByName(trackConfig.name)) {
        trackService.save(
          new Track(
            trackConfig.name,
            trackConfig.description,
            trackConfig.capacity,
            trackConfig.partnerRequired,
            trackConfig.requiredDanceLevel,
            trackConfig.internalId
          )
        );
      } else {
        Track track = trackService.findByName(trackConfig.name);
        track.setDescription(trackConfig.description);
        track.setCapacity(trackConfig.capacity);
        track.setPartnerRequired(trackConfig.partnerRequired);
        track.setRequiredDanceLevel(trackConfig.requiredDanceLevel);
        track.setInternalId(trackConfig.internalId);
        trackService.save(track);
      }
    }
  }
}
