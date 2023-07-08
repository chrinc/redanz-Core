package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.registration.entities.BasePar;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BaseParConfig {
  DOAUTOMATCH("doAutoMatch"  , true, null),
  DOAUTORELEASE("doAutoRelease"  , true, null),
  TESTMAILONLY("testMailOnly"  , false, null),
  DOEODCANCEL("doEODCancel"  , true, null),
  DOEODMATCHING("doEODMatching"  , true, null),
  DOEODRELEASE("doEODRelease"  , true, null),
  DOEODREMINDER("doEODReminder"  , true, null);

  private final String baseParKey;
  private final boolean boolVal;
  private final String stringVal;

  public static void setup(BaseParService baseParService) {

    for (BaseParConfig baseParConfig : BaseParConfig.values()) {
      if (!baseParService.existsByKey(baseParConfig.baseParKey)) {
        baseParService.save(
          new BasePar(
            baseParConfig.baseParKey,
            baseParConfig.boolVal,
            baseParConfig.stringVal
          )
        );
      } else {
        BasePar basePar = baseParService.findByKey(baseParConfig.baseParKey);
        basePar.setBoolValue(baseParConfig.boolVal);
        basePar.setStringValue(baseParConfig.stringVal);
        baseParService.save(basePar);
      }
    }
  }
}
