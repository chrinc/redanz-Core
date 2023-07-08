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
  FUN_LEADER(TrackConfig.FUN_FREE_CHOICE, DanceRoleConfig.LEADER),
  FUN_FOLLOWER(TrackConfig.FUN_FREE_CHOICE, DanceRoleConfig.FOLLOWER),
  FUN_SWITCH(TrackConfig.FUN_FREE_CHOICE, DanceRoleConfig.SWITCH);

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
