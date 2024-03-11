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
  BASIC_LEADER(TrackConfig.BASIC_LEVEL_TRACK, DanceRoleConfig.LEADER),
  BASIC_FOLLOWER(TrackConfig.BASIC_LEVEL_TRACK, DanceRoleConfig.FOLLOWER),
  BASIC_SWITCH(TrackConfig.BASIC_LEVEL_TRACK, DanceRoleConfig.SWITCH),
  ADVANCED_LEADER(TrackConfig.ADVANCED_LEVEL_TRACK, DanceRoleConfig.LEADER),
  ADVANCED_FOLLOWER(TrackConfig.ADVANCED_LEVEL_TRACK, DanceRoleConfig.FOLLOWER),
  ADVANCED_SWITCH(TrackConfig.ADVANCED_LEVEL_TRACK, DanceRoleConfig.SWITCH);
  ;

  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static void setup(TrackService trackService, DanceRoleService danceRoleService) {
    for (TrackDanceRoleConfig trackDanceRoleConfig : TrackDanceRoleConfig.values()) {
      if (!trackService.existsByTrackDanceRole(trackService.findByInternalId(trackDanceRoleConfig.trackConfig.getInternalId()), danceRoleService.findByInternalId(trackDanceRoleConfig.danceRoleConfig.getIntrnalId()))) {
        trackService.save(
          new TrackDanceRole(
            danceRoleService.findByName(trackDanceRoleConfig.getDanceRoleConfig().getName()),
            trackService.findByName(trackDanceRoleConfig.getTrackConfig().getName())
          )
        );
      }
    }
  }
}
