package ch.redanz.redanzCore.model.workshop.config;

import ch.redanz.redanzCore.model.registration.entities.BasePar;
import ch.redanz.redanzCore.model.registration.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.entities.Discount;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
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
      baseParService.save(
        new BasePar(
          baseParConfig.baseParKey,
          baseParConfig.boolVal,
          baseParConfig.stringVal
        )
      );
    }
  }
}
