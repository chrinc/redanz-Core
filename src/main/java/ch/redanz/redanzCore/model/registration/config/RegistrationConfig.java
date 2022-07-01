package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.RegistrationEmailService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.config.BundleConfig;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.config.TrackConfig;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.TrackService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Getter
@AllArgsConstructor
@Slf4j
public enum RegistrationConfig {
  NORMA_REGISTRATION  ( UserConfig.NORMA_USER   ,EventConfig.EVENT2022, BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  WILLIAM_REGISTRATION( UserConfig.WILLIAM_USER ,EventConfig.EVENT2022, BundleConfig.HALFPASS, TrackConfig.FUN_TRACK, DanceRoleConfig.FOLLOWER),
  ARLYNE_REGISTRATION ( UserConfig.ARLYNE_USER  ,EventConfig.EVENT2022, BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.FOLLOWER),
  NAOMI_REGISTRATION  ( UserConfig.NAOMI_USER   ,EventConfig.EVENT2022, BundleConfig.LEVELPASS, TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH),
  ESTHER_REGISTRATION ( UserConfig.ESTHER_USER  ,EventConfig.EVENT2022, BundleConfig.PARTYPASS, null, null),
  EDDIE_REGISTRATION  ( UserConfig.EDDIE_USER   ,EventConfig.EVENT2022, BundleConfig.LEVELPASS, TrackConfig.LINDY_ADVANCED, DanceRoleConfig.LEADER),
  BILLY_REGISTRATION  ( UserConfig.BILLY_USER   ,EventConfig.EVENT2022, BundleConfig.LEVELPASS, TrackConfig.LINDY_INTERMEDIATE, DanceRoleConfig.SWITCH),
  FRANKY_REGISTRATION ( UserConfig.FRANKY_USER  ,EventConfig.EVENT2022, BundleConfig.FULLPASS, TrackConfig.FUN_TRACK, DanceRoleConfig.LEADER),
  CLAUDIA_REGISTRATION( UserConfig.CLAUDIA_USER ,EventConfig.EVENT2022, BundleConfig.HALFPASS, TrackConfig.FUN_TRACK, DanceRoleConfig.FOLLOWER);

  private final UserConfig userConfig;
  private final EventConfig eventConfig;
  private final BundleConfig bundleConfig;
  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;

  public static void setup(
    PersonService personService,
    RegistrationService registrationService,
    BundleService bundleService,
    TrackService trackService,
    DanceRoleService danceRoleService,
    UserService userService,
    EventService eventService,
    RegistrationEmailService registrationEmailService
  ) {

    for (RegistrationConfig registrationConfig : RegistrationConfig.values()) {
      Registration newRegistration;
      if (registrationConfig.trackConfig != null && registrationConfig.danceRoleConfig != null) {
        newRegistration = new Registration(
          personService.findByUser(userService.getUser(registrationConfig.getUserConfig().getEmail())),
          eventService.findByName(registrationConfig.eventConfig.getName()),
          bundleService.findByName(registrationConfig.bundleConfig.getName()),
          trackService.findByName(registrationConfig.trackConfig.getName()),
          danceRoleService.findByName(registrationConfig.danceRoleConfig.getName())
        );
        registrationService.update(newRegistration);

      } else {
        newRegistration =
          new Registration(
            eventService.findByName(registrationConfig.eventConfig.getName()),
            bundleService.findByName(registrationConfig.bundleConfig.getName()),
            personService.findByUser(userService.getUser(registrationConfig.getUserConfig().getEmail()))
          );
        registrationService.update(newRegistration);
      }

      setupEmailRegistration(registrationService, registrationEmailService, newRegistration);
    }
  }

  private static void setupEmailRegistration(
    RegistrationService registrationService,
    RegistrationEmailService registrationEmailService,
    Registration registration
  ) {
    try {
      registrationEmailService.sendRegistrationSubmittedEmail(registration);
    } catch (IOException | TemplateException e) {
      throw new RuntimeException(e);
    }
  }
}
