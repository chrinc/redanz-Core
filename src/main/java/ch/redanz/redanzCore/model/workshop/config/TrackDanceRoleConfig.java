package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.TrackDanceRole;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum TrackDanceRoleConfig {
  LINDY_ADVANCED_LEADER(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.LEADER),
  LINDY_ADVANCED_FOLLOWER(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  LINDY_ADVANCED_SWITCH(TrackConfig.LINDY_ADVANCED, DanceRoleConfig.SWITCH),

  LINDY_INTERMEDIATE_LEADER(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.LEADER),
  LINDY_INTERMEDIATE_FOLLOWER(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.FOLLOWER),
  LINDY_INTERMEDIATE_SWITCH(TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH),

  LINDY_BEGINNER_LEADER(TrackConfig.LINDY_BEGINNER, DanceRoleConfig.LEADER),
  LINDY_BEGINNER_FOLLOWER(TrackConfig.LINDY_BEGINNER, DanceRoleConfig.FOLLOWER),
  LINDY_BEGINNER_SWITCH(TrackConfig.LINDY_BEGINNER, DanceRoleConfig.SWITCH),

  FUN_LEADER(TrackConfig.FUN_TRACK, DanceRoleConfig.LEADER),
  FUN_FOLLOWER(TrackConfig.FUN_TRACK, DanceRoleConfig.FOLLOWER),
  FUN_SWITCH(TrackConfig.FUN_TRACK, DanceRoleConfig.SWITCH);

  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static void setup(TrackService trackService, DanceRoleService danceRoleService) {
    for (TrackDanceRoleConfig trackDanceRoleConfig : TrackDanceRoleConfig.values()) {
      trackService.save(
        new TrackDanceRole(
          danceRoleService.findByName(trackDanceRoleConfig.getDanceRoleConfig().getName()),
          trackService.findByName(trackDanceRoleConfig.getTrackConfig().getName())
        )
      );
    }
  }
}
