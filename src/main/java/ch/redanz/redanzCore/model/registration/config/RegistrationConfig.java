package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.PersonConfig;
import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.config.*;
import ch.redanz.redanzCore.model.workshop.configTest.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Slf4j
public enum RegistrationConfig {
  NORMA_REGISTRATION  (
    UserConfig.NORMA_USER,
    EventConfig.REDANZ_EVENT,
    BundleConfig.HALF_PASS,
    TrackConfig.INTERMEDIATE,
    DanceRoleConfig.FOLLOWER,
    List.of(EventDiscountConfig.EARLY_BIRD),
    null,
    90
  ),
  WILLIAM_REGISTRATION(
    UserConfig.WILLIAM_USER,
    EventConfig.REDANZ_EVENT,
    BundleConfig.HALF_PASS,
    TrackConfig.INTERMEDIATE,
    DanceRoleConfig.FOLLOWER,
    List.of(
      EventDiscountConfig.STUDENT,
      EventDiscountConfig.ABROAD,
      EventDiscountConfig.EARLY_BIRD
    ),
    List.of(
      EventFoodSlotConfig.SAT_VARIETY, EventFoodSlotConfig.SUN_ORIENTAL
    ),
    100
  ),
  ESTHER_REGISTRATION(
    UserConfig.ESTHER_USER,
    EventConfig.REDANZ_EVENT,
    BundleConfig.PARTY_PASS,
    null,
    null,
    null,
    null,
    0
  ),

  OLIVER_REGISTRATION(
    UserConfig.OLIVER_USER
    , EventConfig.REDANZ_EVENT
    , BundleConfig.FULL_PASS
    , TrackConfig.INTERMEDIATE
    , DanceRoleConfig.FOLLOWER
    , List.of(
    EventDiscountConfig.STUDENT
    ,EventDiscountConfig.EARLY_BIRD
    , EventDiscountConfig.ABROAD
  ), null
    , 20
  ),
  HARRY_REGISTRATION(
    UserConfig.HARRY_USER
    ,EventConfig.REDANZ_EVENT
    ,BundleConfig.FULL_PASS
    ,TrackConfig.INTERMEDIATE
    ,DanceRoleConfig.SWITCH
    ,List.of(EventDiscountConfig.STUDENT
    ,EventDiscountConfig.ABROAD
    ,EventDiscountConfig.EARLY_BIRD
  ), null
  , 0
  ),

  FRANKY_REGISTRATION(
    UserConfig.FRANKY_USER,
    EventConfig.REDANZ_EVENT,
    BundleConfig.HALF_PASS,
    TrackConfig.INTERMEDIATE,
    DanceRoleConfig.LEADER,
    List.of(
      EventDiscountConfig.EARLY_BIRD
    ),
    null,
    20
),
  CLAUDIA_REGISTRATION(
    UserConfig.CLAUDIA_USER,
    EventConfig.REDANZ_EVENT,
    BundleConfig.HALF_PASS,
    TrackConfig.INTERMEDIATE,
    DanceRoleConfig.SWITCH,
    List.of(
      EventDiscountConfig.EARLY_BIRD
    ), null,
    160
  );
;
  private final UserConfig userConfig;
  private final EventConfig eventConfig;
  private final BundleConfig bundleConfig;
  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;
  private final List<EventDiscountConfig> eventDiscountConfigList;
  private final List<EventFoodSlotConfig> eventFoodSlotConfigList;
  private final double donationAmount;

  public static void setup(
    PersonService personService,
    RegistrationService registrationService,
    BundleService bundleService,
    TrackService trackService,
    DanceRoleService danceRoleService,
    UserService userService,
    EventService eventService,
    RegistrationEmailService registrationEmailService,
    DiscountService discountService,
    DiscountRegistrationService discountRegistrationService,
    FoodService foodService,
    FoodRegistrationService foodRegistrationService,
    DonationRegistrationService donationRegistrationService
  ) {

    for (RegistrationConfig registrationConfig : RegistrationConfig.values()) {
      Registration newRegistration;
      if (registrationConfig.trackConfig != null && registrationConfig.danceRoleConfig != null) {
        newRegistration = new Registration(
          personService.findByUser(userService.getUser(registrationConfig.getUserConfig().getUsername())),
          eventService.findByName(registrationConfig.eventConfig.getName()),
          bundleService.findByName(registrationConfig.bundleConfig.getName()),
          trackService.findByName(registrationConfig.trackConfig.getName()),
          danceRoleService.findByName(registrationConfig.danceRoleConfig.getName()),
          RegistrationType.PARTICIPANT

        );
      } else {
        newRegistration =
          new Registration(
            eventService.findByName(registrationConfig.eventConfig.getName()),
            bundleService.findByName(registrationConfig.bundleConfig.getName()),
            personService.findByUser(userService.getUser(registrationConfig.getUserConfig().getUsername())),
            RegistrationType.PARTICIPANT
          );
      }
      registrationService.update(newRegistration);

      if (registrationConfig.eventDiscountConfigList != null) {
        registrationConfig.eventDiscountConfigList.forEach(
          discountConfig -> discountRegistrationService.save(
            newRegistration,
            discountService.findByName(discountConfig.getName())
          ));
      }

      if (registrationConfig.eventFoodSlotConfigList != null) {
        registrationConfig.eventFoodSlotConfigList.forEach(
          eventFoodSlotConfig -> {
            foodRegistrationService.save(
              newRegistration
              ,foodService.findByName(eventFoodSlotConfig.getName())
            );
          });
      }

      if (registrationConfig.donationAmount > 0) {
        donationRegistrationService.saveDonationRegistration(
          new DonationRegistration(
            newRegistration,
            registrationConfig.donationAmount
          )
        );
      }
      setupEmailRegistration(registrationEmailService, newRegistration);
    }
  }

  private static void setupEmailRegistration(
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
