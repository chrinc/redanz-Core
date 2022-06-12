package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackDanceRoleConfig {
  LINDY_ADVANCED_LEADER(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.LEADER),
  LINDY_ADVANCED_FOLLOWER(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  LINDY_ADVANCED_SWITCH(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.SWITCH),

  LINDY_INTERMEDIATE_LEADER(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.LEADER),
  LINDY_INTERMEDIATE_FOLLOWER(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.FOLLOWER),
  LINDY_INTERMEDIATE_SWITCH(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH);

  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static List<TrackDanceRole> setup(TrackRepo trackRepo, DanceRoleRepo danceRoleRepo) {
    List<TrackDanceRole> transitions = new ArrayList<>();

    for (TrackDanceRoleConfig trackDanceRoleConfig : TrackDanceRoleConfig.values()) {
      transitions.add(
        new TrackDanceRole(
          danceRoleRepo.findByName(trackDanceRoleConfig.getDanceRoleConfig().getName()),
          trackRepo.findByName(trackDanceRoleConfig.getTrackConfig().getName())
        )
      );
    }
    return transitions;
  }
}
