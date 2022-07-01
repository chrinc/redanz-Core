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
  LINDY_ADVANCED("Exploring FUNdamentals", "", 10, true, DanceLevel.ADVANCED),
  LINDY_INTERMEDIATE("Expanding FUNdamentals", "", 10, true, DanceLevel.INTERMEDIATE),
  LINDY_BEGINNER("Building FUNdamentals", "", 10, true, DanceLevel.BEGINNER),
  FUN_TRACK("FUN Track", "", 999, true, null);

  private final String name;
  private final String description;
  private final Integer capacity;
  private final Boolean partnerRequired;
  private final DanceLevel requiredDanceLevel;

  public static void setup(TrackService trackService) {
    for (TrackConfig trackConfig : TrackConfig.values()) {
      trackService.save(
        new Track(
          trackConfig.getName(),
          trackConfig.getDescription(),
          trackConfig.getCapacity(),
          trackConfig.getPartnerRequired(),
          trackConfig.getRequiredDanceLevel()
        )
      );
    }
  }
}
