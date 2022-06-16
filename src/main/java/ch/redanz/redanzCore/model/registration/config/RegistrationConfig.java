package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackRepo;
import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.workshop.config.BundleConfig;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.TrackConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Slf4j
public enum RegistrationConfig {
  NORMA_REGISTRATION  ( UserConfig.NORMA_USER   ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  WILLIAM_REGISTRATION( UserConfig.WILLIAM_USER ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.SOLOJAZZ, null),
//  ELNORA_REGISTRATION ( UserConfig.ELNORA_USER  ,EventConfig.EVENT2022, BundleConfig.HALFPASS, TrackConfig.SOLOJAZZ, null),
  ARLYNE_REGISTRATION ( UserConfig.ARLYNE_USER  ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  NAOMI_REGISTRATION  ( UserConfig.NAOMI_USER   ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH),
  ESTHER_REGISTRATION ( UserConfig.ESTHER_USER  ,EventConfig.EVENT2022, BundleConfig.PARTYPASS, null, null),
  EDDIE_REGISTRATION  ( UserConfig.EDDIE_USER   ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.LEADER),
  BILLY_REGISTRATION  ( UserConfig.BILLY_USER   ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH),
  FRANKY_REGISTRATION ( UserConfig.FRANKY_USER  ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.LEADER),
  CLAUDIA_REGISTRATION( UserConfig.CLAUDIA_USER ,EventConfig.EVENT2022, BundleConfig.HALFPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER);


  private final UserConfig userConfig;
  private final EventConfig eventConfig;
  private final BundleConfig bundleConfig;
  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static List<Registration> setup(
    PersonRepo personRepo,
    EventRepo eventRepo,
    BundleRepo bundleRepo,
    TrackRepo trackRepo,
    DanceRoleRepo danceRoleRepo,
    UserRepo userRepo
  ) {
    List<Registration> registrations = new ArrayList<>();

    for (RegistrationConfig registrationConfig : RegistrationConfig.values()) {
      if (registrationConfig.trackConfig != null && registrationConfig.danceRoleConfig != null) {
        registrations.add(
          new Registration(
            personRepo.findByUser(userRepo.findByEmail(registrationConfig.getUserConfig().getEmail())),
            eventRepo.findByName(registrationConfig.eventConfig.getName()),
            bundleRepo.findByName(registrationConfig.bundleConfig.getName()),
            trackRepo.findByName(registrationConfig.trackConfig.getName()),
            danceRoleRepo.findByName(registrationConfig.danceRoleConfig.getName())
          )
        );
      } else {
        registrations.add(
          new Registration(
            eventRepo.findByName(registrationConfig.eventConfig.getName()),
            bundleRepo.findByName(registrationConfig.bundleConfig.getName()),
            personRepo.findByUser(userRepo.findByEmail(registrationConfig.getUserConfig().getEmail()))
          )
        );
      }
    }
    return registrations;
  }
}
