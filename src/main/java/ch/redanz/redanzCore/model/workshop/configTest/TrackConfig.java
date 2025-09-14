package ch.redanz.redanzCore.model.workshop.configTest;

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
  ADVANCED("Advanced Track", OutTextConfig.LABEL_ADVANCED_DESC_EN.getOutTextKey(), true, false,null),
  INTERMEDIATE("Intermediate Track", OutTextConfig.LABEL_INTERMEDIATE_DESC_EN.getOutTextKey(), true, false, null),
  NO_LEVEL("Solo Jazz Track", OutTextConfig.LABEL_SOLO_JAZZ_DESC_EN.getOutTextKey(), true, false, null),
  ;

  private final String name;
  private final String description;
//  private final Integer capacity;
  private final Boolean partnerRequired;
  private final Boolean ownPartnerRequired;
  private final DanceLevel requiredDanceLevel;

  public static void setup(TrackService trackService) {
    for (TrackConfig trackConfig : TrackConfig.values()) {
      if (!trackService.existsByName(trackConfig.name)) {
        trackService.save(
          new Track(
            trackConfig.name,
            trackConfig.description,
//            trackConfig.capacity,
            trackConfig.partnerRequired,
            trackConfig.ownPartnerRequired,
            trackConfig.requiredDanceLevel
          )
        );
      } else {
        Track track = trackService.findByName(trackConfig.name);
        track.setDescription(trackConfig.description);
//        track.setCapacity(trackConfig.capacity);
        track.setPartnerRequired(trackConfig.partnerRequired);
        track.setOwnPartnerRequired(trackConfig.ownPartnerRequired);
        track.setRequiredDanceLevel(trackConfig.requiredDanceLevel);
        trackService.save(track);
      }
    }
  }
}
