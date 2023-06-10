package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.profile.service.UserService;
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
  NORMA_REGISTRATION  ( UserConfig.NORMA_USER.getEmail()   ,"franky_user@gmail.com", EventConfig.EVENT2023),
  WILLIAM_REGISTRATION( UserConfig.WILLIAM_USER.getEmail() ,null , EventConfig.EVENT2023),
  ARLYNE_REGISTRATION ( UserConfig.ARLYNE_USER.getEmail()  ,null , EventConfig.EVENT2023),
  NAOMI_REGISTRATION  ( UserConfig.NAOMI_USER.getEmail()   ,"billy_user@gmail.com" , EventConfig.EVENT2023),
  EDDIE_REGISTRATION  ( UserConfig.EDDIE_USER.getEmail()   ,null , EventConfig.EVENT2023),
  OLIVER_REGISTRATION ( UserConfig.OLIVER_USER.getEmail()  ,null , EventConfig.EVENT2023),
  HARRY_REGISTRATION  ( UserConfig.HARRY_USER.getEmail()   ,null,  EventConfig.EVENT2023),
  BILLY_REGISTRATION  ( UserConfig.BILLY_USER.getEmail()   ,"naomi_user@gmail.com" , EventConfig.EVENT2023),
  FRANKY_REGISTRATION ( UserConfig.FRANKY_USER.getEmail()  ,"norma_user@gmail.com", EventConfig.EVENT2023),
  CLAUDIA_REGISTRATION( UserConfig.CLAUDIA_USER.getEmail() ,null,  EventConfig.EVENT2023);

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
            eventService.findByName(registrationMatchingConfig.eventConfig.getName())
          ).get(),
          registrationMatchingConfig.partnerEmail
        )
      );
    }
  }
}
