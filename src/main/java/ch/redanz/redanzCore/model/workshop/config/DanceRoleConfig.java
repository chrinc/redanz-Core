package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum DanceRoleConfig {
  LEADER("Leader", "", "lead", "l"),
  FOLLOWER("Follower", "", "follow", "f"),
  SWITCH("Switch", "", "switch", "s");


  private final String name;
  private final String description;
  private final String intrnalId;
  private final String abbreviation;

  public static void setup(DanceRoleService danceRoleService) {

    for (DanceRoleConfig danceRoleConfig : DanceRoleConfig.values()) {
      if (!danceRoleService.existsByName(danceRoleConfig.name)) {
        danceRoleService.save(
          new DanceRole(
            danceRoleConfig.name,
            danceRoleConfig.description,
            danceRoleConfig.intrnalId,
            danceRoleConfig.abbreviation
          )
        );
      } else {
        DanceRole danceRole = danceRoleService.findByName(danceRoleConfig.name);
        danceRole.setDescription(danceRoleConfig.description);
        danceRole.setInternalId(danceRoleConfig.intrnalId);
        danceRole.setAbbreviation(danceRoleConfig.abbreviation);
        danceRoleService.save(danceRole);
      }
    }
  }
}
