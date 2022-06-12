package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.DanceLevel;
import ch.redanz.redanzCore.model.workshop.Track;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackConfig {
  SOLOJAZZ("Solo Jazz", "Solo Jazz Description", 40, false, null),
  LINDY_ADVANCED("Lindy Advanced", "", 20, true, DanceLevel.ADVANCED),
  LINDY_INTERMEDIATE("Lindy Intermediate", "", 60, true, DanceLevel.INTERMEDIATE);

  private final String name;
  private final String description;
  private final Integer capacity;
  private final Boolean partnerRequired;
  private final DanceLevel requiredDanceLevel;

  public static List<Track> setup() {
    List<Track> transitionList = new ArrayList<>();

    for (TrackConfig trackConfig : TrackConfig.values()) {
      transitionList.add(
        new Track(
          trackConfig.getName(),
          trackConfig.getDescription(),
          trackConfig.getCapacity(),
          trackConfig.getPartnerRequired(),
          trackConfig.getRequiredDanceLevel()
        )
      );
    }
    return transitionList;
  }
}
