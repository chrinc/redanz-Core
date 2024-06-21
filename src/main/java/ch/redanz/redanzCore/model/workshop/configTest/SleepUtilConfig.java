package ch.redanz.redanzCore.model.workshop.configTest;


import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import ch.redanz.redanzCore.model.workshop.service.SleepUtilService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum SleepUtilConfig {
  SLEEP_UTIL_MATTRESS_DOUBLE(OutTextConfig.LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_EN.getOutTextKey()),
  SLEEP_UTIL_MATTRESS_SINGLE(OutTextConfig.LABEL_SLEEP_UTIL_MATTRESS_SINGLE_EN.getOutTextKey()),
  SLEEP_UTIL_COUCH(OutTextConfig.LABEL_SLEEP_UTIL_COUCH_EN.getOutTextKey()),
  SLEEP_UTIL_MAT(OutTextConfig.LABEL_SLEEP_UTIL_MAT_EN.getOutTextKey()),
  SLEEP_UTIL_BAG(OutTextConfig.LABEL_SLEEP_UTIL_BAG_EN.getOutTextKey());

  private final String name;

  public static void setup(SleepUtilService sleepUtilService) {
    for (SleepUtilConfig sleepUtilConfig : SleepUtilConfig.values()) {
      if (!sleepUtilService.existsByName(sleepUtilConfig.getName())) {
        sleepUtilService.save(
          new SleepUtil(
            sleepUtilConfig.getName()
          )
        );
      }
    }
  }
}
