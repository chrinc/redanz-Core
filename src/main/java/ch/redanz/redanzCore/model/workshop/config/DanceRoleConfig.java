package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum DanceRoleConfig {
  LEADER("Leader", ""),
  FOLLOWER("Follower", ""),
  SWITCH("Switch", "");


  private final String name;
  private final String description;

  DanceRoleConfig(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public static void setup(DanceRoleService danceRoleService) {

    for (DanceRoleConfig danceRoleConfig : DanceRoleConfig.values()) {
      danceRoleService.save(
        new DanceRole(
          danceRoleConfig.getName(),
          danceRoleConfig.getDescription()
        )
      );
    }
  }
}
