package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.UserRole;
import ch.redanz.redanzCore.model.profile.User;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum UserConfig {

  // Partizipants
  FRANKY_USER ("franky_user@gmail.com" , "password", UserRole.PARTICIPANT, false, true),
  CLAUDIA_USER("claudia_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  NORMA_USER  ("norma_user@gmail.com"  , "password", UserRole.PARTICIPANT, false, true),
  EDDIE_USER  ("eddie_user@gmail.com"  , "password", UserRole.PARTICIPANT, false, true),
  WILLIAM_USER("william_user@gmail.com", "password", UserRole.PARTICIPANT, false, true),
  ELNORA_USER ("elnora_user@gmail.com" , "password", UserRole.PARTICIPANT, false, true),
  ARLYNE_USER ("arlyne_user@gmail.com" , "password", UserRole.PARTICIPANT, false, true),
  BILLY_USER  ("billy_user@gmail.com"  , "password", UserRole.PARTICIPANT, false, true),
  NAOMI_USER  ("naomi_user@gmail.com"  , "password", UserRole.PARTICIPANT, false, true),
  ESTHER_USER ("esther_user@gmail.com" , "password", UserRole.PARTICIPANT, false, true),

  // Organizers
  ORG_SONNY_USER("org_sonny_user@gmail.com", "password", UserRole.ORGANIZER, false, true),
  ORG_ANN_USER("org_ann_user@gmail.com", "password", UserRole.ORGANIZER, false, true);

  BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
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

  public static List<User> setup() {
    List<User> transitionList = new ArrayList<>();

    for (UserConfig roleConfig : UserConfig.values()) {
      transitionList.add(
        new User(
          roleConfig.getEmail(),
          roleConfig.getPassword(),
          roleConfig.getUserRole(),
          roleConfig.getLocked(),
          roleConfig.getEnabled()
        ));
    }
    return transitionList;
  }
}
