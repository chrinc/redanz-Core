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
    List.of(DiscountConfig.EARLY_BIRD),
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
      DiscountConfig.STUDENT,
      DiscountConfig.ABROAD,
      DiscountConfig.EARLY_BIRD
    ),
    List.of(
      Map.of(FoodConfig.FOOD_VARIETY, SlotConfig.SLOT_SATURDAY_LUNCH),
      Map.of(FoodConfig.FOOD_ORIENTAL, SlotConfig.SLOT_SUNDAY_LUNCH)
    ),
    100
  ),
  ESTHER_REGISTRATION(UserConfig.ESTHER_USER,
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
    DiscountConfig.STUDENT
    ,DiscountConfig.EARLY_BIRD
    , DiscountConfig.ABROAD
  ), null
    , 20
  ),
  HARRY_REGISTRATION(
    UserConfig.HARRY_USER
    ,EventConfig.REDANZ_EVENT
    ,BundleConfig.FULL_PASS
    ,TrackConfig.INTERMEDIATE
    ,DanceRoleConfig.SWITCH
    ,List.of(DiscountConfig.STUDENT
    ,DiscountConfig.ABROAD
    ,DiscountConfig.EARLY_BIRD
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
      DiscountConfig.EARLY_BIRD
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
      DiscountConfig.EARLY_BIRD
    ), null,
    160
  );
;
  private final UserConfig userConfig;
  private final EventConfig eventConfig;
  private final BundleConfig bundleConfig;
  private final TrackConfig trackConfig;
  private final DanceRoleConfig danceRoleConfig;
  private final List<DiscountConfig> discountConfigList;
  private final List<Map<FoodConfig, SlotConfig>> foodSlotConfigList;
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
    SlotService slotService,
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

      if (registrationConfig.discountConfigList != null) {
        registrationConfig.discountConfigList.forEach(
          discountConfig -> discountRegistrationService.save(
            newRegistration,
            discountService.findByName(discountConfig.getName())
          ));
      }

      if (registrationConfig.foodSlotConfigList != null) {
        registrationConfig.foodSlotConfigList.forEach(
          foodSlotConfig -> {
            FoodConfig foodConfig = (FoodConfig) foodSlotConfig.keySet().toArray()[0];
            SlotConfig slotConfig = (SlotConfig) foodSlotConfig.values().toArray()[0];
            foodRegistrationService.save(
              newRegistration
              ,foodService.findByName(foodConfig.getName())
              ,slotService.findByName(slotConfig.getName())
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
