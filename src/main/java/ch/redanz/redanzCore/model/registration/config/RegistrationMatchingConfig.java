package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public enum RegistrationMatchingConfig {
  NORMA_REGISTRATION  ( UserConfig.NORMA_USER.getUsername()   ,"franky_user@gmail.com", EventConfig.REDANZ_EVENT),
  WILLIAM_REGISTRATION( UserConfig.WILLIAM_USER.getUsername() ,null , EventConfig.REDANZ_EVENT),
//  ARLYNE_REGISTRATION ( UserConfig.ARLYNE_USER.getUsername()  ,null , EventConfig.REDANZ_WORKSHOP),
//  NAOMI_REGISTRATION  ( UserConfig.NAOMI_USER.getUsername()   ,"billy_user@gmail.com" , EventConfig.REDANZ_WORKSHOP),
//  EDDIE_REGISTRATION  ( UserConfig.EDDIE_USER.getUsername()   ,null , EventConfig.EVENT2023),
  OLIVER_REGISTRATION ( UserConfig.OLIVER_USER.getUsername()  ,null , EventConfig.REDANZ_EVENT),
  HARRY_REGISTRATION  ( UserConfig.HARRY_USER.getUsername()   ,null,  EventConfig.REDANZ_EVENT),
//  BILLY_REGISTRATION  ( UserConfig.BILLY_USER.getUsername()   ,"naomi_user@gmail.com" , EventConfig.EVENT2023),
  FRANKY_REGISTRATION ( UserConfig.FRANKY_USER.getUsername()  ,"norma_user@gmail.com", EventConfig.REDANZ_EVENT)
//  CLAUDIA_REGISTRATION( UserConfig.CLAUDIA_USER.getUsername() ,null,  EventConfig.EVENT2023);
;
  private final String user1Email;
  private final String partnerEmail;
  private final EventConfig eventConfig;

  public static void setup(
    EventService eventService,
    RegistrationService registrationService,
    PersonService personService,
    UserService userService,
    RegistrationMatchingService registrationMatchingService
  ) {
    for (RegistrationMatchingConfig registrationMatchingConfig : RegistrationMatchingConfig.values()) {
      registrationMatchingService.save(
        new RegistrationMatching(
          registrationService.findByParticipantAndEvent(
            personService.findByUser(userService.getUser(registrationMatchingConfig.user1Email)),
            eventService.findByName(registrationMatchingConfig.eventConfig.getName()),
            RegistrationType.PARTICIPANT
          ).get(),
          registrationMatchingConfig.partnerEmail
        )
      );
    }
  }
}
