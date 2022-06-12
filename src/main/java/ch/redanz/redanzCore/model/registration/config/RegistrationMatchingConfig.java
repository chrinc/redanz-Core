package ch.redanz.redanzCore.model.registration.config;

import ch.redanz.redanzCore.model.profile.config.UserConfig;
import ch.redanz.redanzCore.model.profile.repository.PersonRepo;
import ch.redanz.redanzCore.model.profile.repository.UserRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.registration.repository.RegistrationRepo;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@Slf4j
public enum RegistrationMatchingConfig {
  FRANKY_REGISTRATION ( UserConfig.FRANKY_USER.getEmail()  ,"norma_user@gmail.com", EventConfig.EVENT2022),
  NORMA_REGISTRATION  ( UserConfig.NORMA_USER.getEmail()   ,"franky_user@gmail.com", EventConfig.EVENT2022),
  EDDIE_REGISTRATION  ( UserConfig.EDDIE_USER.getEmail()   ,"" , EventConfig.EVENT2022),
  ARLYNE_REGISTRATION ( UserConfig.ARLYNE_USER.getEmail()  ,"" , EventConfig.EVENT2022),
  BILLY_REGISTRATION  ( UserConfig.BILLY_USER.getEmail()   ,"naomi_user@gmail.com" , EventConfig.EVENT2022),
  NAOMI_REGISTRATION  ( UserConfig.NAOMI_USER.getEmail()   ,"billy_user@gmail.com" , EventConfig.EVENT2022),
  CLAUDIA_REGISTRATION( UserConfig.CLAUDIA_USER.getEmail() ,"",  EventConfig.EVENT2022);

  private final String user1Email;
  private final String partnerEmail;
  private final EventConfig eventConfig;

  public static List<RegistrationMatching> setup(
    EventRepo eventRepo,
    RegistrationRepo registrationRepo,
    PersonRepo personRepo,
    UserRepo userRepo
  ) {
    List<RegistrationMatching> registrationMatchings = new ArrayList<>();

    for (RegistrationMatchingConfig registrationMatchingConfig : RegistrationMatchingConfig.values()) {
      registrationMatchings.add(
        new RegistrationMatching(
          registrationRepo.findByParticipantAndEvent(
            personRepo.findByUser(userRepo.findByEmail(registrationMatchingConfig.user1Email)),
            eventRepo.findByName(registrationMatchingConfig.eventConfig.getName())
          ).get(),
          registrationMatchingConfig.partnerEmail
        )
      );
    }
    return registrationMatchings;
  }
}