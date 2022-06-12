package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.DanceRole;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
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

  public static List<DanceRole> setup() {
    List<DanceRole> transitions = new ArrayList<>();

    for (DanceRoleConfig danceRoleConfig : DanceRoleConfig.values()) {
      transitions.add(
        new DanceRole(
          danceRoleConfig.getName(),
          danceRoleConfig.getDescription()
        )
      );
    }
    return transitions;
  }
}
