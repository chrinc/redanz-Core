package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Track;
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
  INTERMEDIATE_LEADER(TrackConfig.INTERMEDIATE, DanceRoleConfig.LEADER, null),
  INTERMEDIATE_FOLLOWER(TrackConfig.INTERMEDIATE, DanceRoleConfig.FOLLOWER, null),
  INTERMEDIATE_SWITCH(TrackConfig.INTERMEDIATE, DanceRoleConfig.SWITCH, OutTextConfig.LABEL_SWITCH_LEVEL_HINT_EN.getOutTextKey()),

  ADVANCED_LEADER(TrackConfig.ADVANCED, DanceRoleConfig.LEADER, null),
  ADVANCED_FOLLOWER(TrackConfig.ADVANCED, DanceRoleConfig.FOLLOWER, null),
  ADVANCED_SWITCH(TrackConfig.ADVANCED, DanceRoleConfig.SWITCH, OutTextConfig.LABEL_SWITCH_LEVEL_HINT_EN.getOutTextKey()),

  NO_LEVEL_LEADER(TrackConfig.NO_LEVEL, DanceRoleConfig.LEADER, null),
  NO_LEVEL_FOLLOWER(TrackConfig.NO_LEVEL, DanceRoleConfig.FOLLOWER, null),
  NO_LEVEL_SWITCH(TrackConfig.NO_LEVEL, DanceRoleConfig.SWITCH, null);

  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;
  private final String hint;

  public static void setup(TrackService trackService, DanceRoleService danceRoleService) {
    for (TrackDanceRoleConfig trackDanceRoleConfig : TrackDanceRoleConfig.values()) {
      Track track = trackService.findByName(trackDanceRoleConfig.getTrackConfig().getName());
      DanceRole danceRole = danceRoleService.findByName(trackDanceRoleConfig.getDanceRoleConfig().getName());

//      track.getTrackDanceRoles().add(danceRole);
      trackService.save(new TrackDanceRole(
        track, danceRole, trackDanceRoleConfig.getHint()
      ));
    }
  }
}
