package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.service.UserService;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public enum UserConfig {

  // PARTICIPANTS
  FRANKY_USER("franky_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  CLAUDIA_USER("claudia_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  NORMA_USER("norma_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  EDDIE_USER("eddie_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  WILLIAM_USER("william_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  ELNORA_USER("elnora_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  ARLYNE_USER("arlyne_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  BILLY_USER("billy_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  NAOMI_USER("naomi_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  ESTHER_USER("esther_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  ANN_USER("ann_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  MILDRED_USER("mildred_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  RUTHIE_USER("ruthie_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  WILLA_USER("willa_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  HARRY_USER("harry_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  OLIVER_USER("oliver_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),

  // ORGANIZERS
  ORG_SONNY_USER("org_sonny_user@gmail.com", "password", UserRole.ORGANIZER, false, true),
  ORG_ANN_USER("org_ann_user@gmail.com", "password", UserRole.ORGANIZER, false, true);

  final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
  private final String email;
  private final String password;
  private final UserRole userRole;
  private final Boolean locked;
  private final Boolean enabled;

  UserConfig(String email, String password, UserRole userRole, Boolean locked, Boolean enabled) {
    this.email = email;
    this.password = bCryptPasswordEncoder.encode(password);
    this.userRole = userRole;
    this.locked = locked;
    this.enabled = enabled;
  }

  public static void setup(UserService userService) {

    for (UserConfig roleConfig : UserConfig.values()) {
      userService.save(
        new User(
          roleConfig.getEmail(),
          roleConfig.getPassword(),
          roleConfig.getUserRole(),
          roleConfig.getLocked(),
          roleConfig.getEnabled()
        )
      );
    }
  }
}
