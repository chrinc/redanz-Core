package ch.redanz.redanzCore.model.profile.config;

import ch.redanz.redanzCore.model.profile.entities.FieldProperty;
import ch.redanz.redanzCore.model.profile.entities.User;
import ch.redanz.redanzCore.model.profile.entities.UserRole;
import ch.redanz.redanzCore.model.profile.service.FieldPropertyService;
import ch.redanz.redanzCore.model.profile.service.UserService;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
public enum FieldPropertyConfig {

  // PARTICIPANTS
  FIRST_NAME("firstName", OutTextConfig.LABEL_FIRST_NAME_EN, true, true, "text", ""),
  LAST_NAME("lastName", OutTextConfig.LABEL_LAST_NAME_EN,true, true, "text", ""),
  PRONOUNS("pronouns", OutTextConfig.LABEL_PRONOUNS_EN, false, false, "text", ""),
  STREET("street", OutTextConfig.LABEL_STREET_EN, true, true, "text", ""),
  POSTAL_CODE("postalCode", OutTextConfig.LABEL_POSTAL_CODE_EN, true , true, "text", ""),
  CITY("city", OutTextConfig.LABEL_CITY_EN, true, false, "text", ""),
  COUNTRY("country", OutTextConfig.LABEL_COUNTRY_EN, true, true, "list", "country"),
  LANGUAGE("language", OutTextConfig.LABEL_LANGUAGE_EN, true, true, "list", "language");


  private final String fieldKey;
  private final OutTextConfig name;
  private final Boolean active;
  private final Boolean required;
  private final String type;
  private final String listSource;

  FieldPropertyConfig(String fieldKey, OutTextConfig name, Boolean active, Boolean required, String type, String listSource) {
    this.fieldKey = fieldKey;
    this.name = name;
    this.active = active;
    this.required = required;
    this.type = type;
    this.listSource = listSource;
  }

  public static void setup(FieldPropertyService fieldPropertyService) {
    for (FieldPropertyConfig roleConfig : FieldPropertyConfig.values()) {
      if (!fieldPropertyService.existsByKey(roleConfig.fieldKey)) {
        fieldPropertyService.save(
          new FieldProperty(
            roleConfig.fieldKey,
            roleConfig.name.getOutTextKey(),
            roleConfig.active,
            roleConfig.required,
            roleConfig.type,
            roleConfig.listSource
          )
        );
      }
    }
  }
}
