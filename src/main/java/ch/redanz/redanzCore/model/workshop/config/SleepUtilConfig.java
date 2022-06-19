package ch.redanz.redanzCore.model.workshop.config;


import ch.redanz.redanzCore.model.workshop.Food;
import ch.redanz.redanzCore.model.workshop.SleepUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum SleepUtilConfig {
  SLEEP_UTIL_MATTRESS_DOUBLE(OutTextConfig.LABEL_SLEEP_UTIL_MATTRESS_DOUBLE_EN.getOutTextKey()),
  SLEEP_UTIL_MATTRESS_SINGLE(OutTextConfig.LABEL_SLEEP_UTIL_MATTRESS_SINGLE_EN.getOutTextKey()),
  SLEEP_UTIL_COUCH(OutTextConfig.LABEL_SLEEP_UTIL_COUCH_EN.getOutTextKey()),
  SLEEP_UTIL_MAT(OutTextConfig.LABEL_SLEEP_UTIL_MAT_EN.getOutTextKey()),
  SLEEP_UTIL_BAG(OutTextConfig.LABEL_SLEEP_UTIL_BAG_EN.getOutTextKey());

  private String name;

  public static List<SleepUtil> setup() {
    List<SleepUtil> transitionList = new ArrayList<>();

    for (SleepUtilConfig sleepUtilConfig : SleepUtilConfig.values()) {
      transitionList.add(
        new SleepUtil(
          sleepUtilConfig.getName()
        )
      );
    }
    return transitionList;
  }
}
