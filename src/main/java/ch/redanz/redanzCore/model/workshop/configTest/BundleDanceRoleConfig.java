package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BundleDanceRoleConfig {
  FULL_LEADER(BundleConfig.FULL_PASS, DanceRoleConfig.LEADER),
  FULL_FOLLOWER(BundleConfig.FULL_PASS, DanceRoleConfig.FOLLOWER),
  FULL_SWITCH(BundleConfig.FULL_PASS, DanceRoleConfig.SWITCH),

  HALF_LEADER(BundleConfig.HALF_PASS, DanceRoleConfig.LEADER),
  HALF_FOLLOWER(BundleConfig.HALF_PASS, DanceRoleConfig.FOLLOWER),
  HALF_SWITCH(BundleConfig.HALF_PASS, DanceRoleConfig.SWITCH),
  ;

  private final BundleConfig bundleConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static void setup(BundleService bundleService, DanceRoleService danceRoleService) {
    for (BundleDanceRoleConfig bundleDanceRoleConfig : BundleDanceRoleConfig.values()) {
//      log.info("inc@setubBundleDance getBundleConfig().getName(): " + bundleDanceRoleConfig.getBundleConfig().getName());
//      log.info("inc@setubBundleDance getDanceRoleConfig().getName(): " + bundleDanceRoleConfig.getDanceRoleConfig().getName());
//      log.info("inc@setubBundleDance Roles");
      Bundle bundle = bundleService.findByName(bundleDanceRoleConfig.getBundleConfig().getName());
      DanceRole danceRole = danceRoleService.findByName(bundleDanceRoleConfig.getDanceRoleConfig().getName());
//      track.getTrackDanceRoles().add(danceRole);
//      bundleService.save(new EventDanceRole(
//        bundle, danceRole
//      ));
    }
  }
}
