package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.DanceLevel;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrivateClassConfig {
  NORMA("Norma", OutTextConfig.LABEL_PRIVATE_NORMA_DESC_EN.getOutTextKey(), false, null);

  private final String name;
  private final String description;
  private final Boolean partnerRequred;
  private final DanceLevel requiredDanceLevel;

  public static void setup(PrivateClassService privateClassService) {
    for (PrivateClassConfig privateClassConfig : PrivateClassConfig.values()) {
      if (!privateClassService.existsByName(privateClassConfig.getName())) {
        privateClassService.save(
          new PrivateClass(
            privateClassConfig.name,
            privateClassConfig.description,
            privateClassConfig.partnerRequred,
            privateClassConfig.requiredDanceLevel
          )
        );
      } else {
        PrivateClass privateClass = privateClassService.findByName(privateClassConfig.getName());
        privateClass.setDescription(privateClassConfig.description);
        privateClass.setPartnerRequired(privateClassConfig.partnerRequred);
        privateClass.setRequiredDanceLevel(privateClassConfig.requiredDanceLevel);
        privateClassService.save(privateClass);
      }
    }
  }

}
