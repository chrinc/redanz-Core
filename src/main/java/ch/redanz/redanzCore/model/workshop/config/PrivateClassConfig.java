package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.workshop.entities.DanceLevel;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrivateClassConfig {
  ELZE("Elze", OutTextConfig.LABEL_PRIVATE_ELZE_DESC_EN.getOutTextKey(), 150, false, null, 1),
  SONJA_HECTOR("Sonja, Hector", OutTextConfig.LABEL_PRIVATE_SONJA_HECTOR_DESC_EN.getOutTextKey(), 170, false, null, 1),
  BARA("Bara", OutTextConfig.LABEL_PRIVATE_BARA_DESC_EN.getOutTextKey(), 130, false, null, 1),
  CLAUDIA("Claudia", OutTextConfig.LABEL_PRIVATE_CLAUDIA_DESC_EN.getOutTextKey(), 100, false, null, 1);

  private final String name;
  private final String description;
  private final double price;
  private final Boolean partnerRequred;
  private final DanceLevel requiredDanceLevel;
  private final Integer capacity;

  public static void setup(PrivateClassService privateClassService) {
    for (PrivateClassConfig privateClassConfig : PrivateClassConfig.values()) {
      if (!privateClassService.existsByName(privateClassConfig.getName())) {
        privateClassService.save(
          new PrivateClass(
            privateClassConfig.name,
            privateClassConfig.description,
            privateClassConfig.capacity,
            privateClassConfig.price,
            privateClassConfig.partnerRequred,
            privateClassConfig.requiredDanceLevel
          )
        );
      } else {
        PrivateClass privateClass = privateClassService.findByName(privateClassConfig.getName());
        privateClass.setCapacity(privateClassConfig.capacity);
        privateClass.setDescription(privateClassConfig.description);
        privateClass.setPrice(privateClassConfig.price);
        privateClass.setPartnerRequired(privateClassConfig.partnerRequred);
        privateClass.setRequiredDanceLevel(privateClassConfig.requiredDanceLevel);
        privateClassService.save(privateClass);
      }
    }
  }

}
