package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.BasePar;
import ch.redanz.redanzCore.model.workshop.repository.BaseParRepo;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class BaseParService {
  private final BaseParRepo baseParRepo;
  private final OutTextService outTextService;

  public boolean existsByNameAndEvent(String name, Event event) {
    return baseParRepo.existsByEventAndName(event, name);
  }

  public boolean doAutoMatch(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_AUTO_MATCH_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_AUTO_MATCH_EN.getOutTextKey()).getVal()) :
      false; // default: false
  }

  public boolean doAutoRelease(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_AUTO_RELEASE_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_AUTO_RELEASE_EN.getOutTextKey()).getVal()) :
      false; // default: false
  }

  public boolean doEODCancel(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_EOD_CANCEL_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_EOD_CANCEL_EN.getOutTextKey()).getVal()) :
      false; // default: false
  }

  public boolean doEODMatching(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_EOD_MATCHING_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_EOD_MATCHING_EN.getOutTextKey()).getVal()) :
      true; // default: false
  }

  public boolean doEODRelease(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_EOD_RELEASE_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_EOD_RELEASE_EN.getOutTextKey()).getVal()) :
      false; // default: false
  }

  public Integer waitListLength(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_WAIT_LIST_LENGTH_EN.getOutTextKey(), event) ?
      Integer.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_WAIT_LIST_LENGTH_EN.getOutTextKey()).getVal()) :
      0; // default: 0
  }

  public boolean doEODReminder(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_EOD_REMINDER_EN.getOutTextKey(), event) ?
      Boolean.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_EOD_REMINDER_EN.getOutTextKey()).getVal()) :
      true; // default: true
  }

  public Integer cancelAfterDays(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_CANCEL_AFTER_DAYS_EN.getOutTextKey(), event) ?
      Integer.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_CANCEL_AFTER_DAYS_EN.getOutTextKey()).getVal()) :
      5; // default: 5
  }

  public Integer reminderAfterDays(Event event) {
    return existsByNameAndEvent(OutTextConfig.LABEL_BASE_PAR_REMINDER_AFTER_DAYS_EN.getOutTextKey(), event) ?
      Integer.valueOf(baseParRepo.findByEventAndName(event, OutTextConfig.LABEL_BASE_PAR_REMINDER_AFTER_DAYS_EN.getOutTextKey()).getVal()) :
      5; // default: 5
  }

  public void save(BasePar basePar) {
    baseParRepo.save(basePar);
  }

  public List<Map<String, Object>> getData(Event event) {
    List<Map<String, Object>> baseParList = new ArrayList<>();

    baseParRepo.findAllByEvent(event).forEach(basePar -> {
      Map<String, Object> baseParMap = new HashMap<>();
      baseParMap.put("name", outTextService.getOutTextMapByKey(basePar.getName()));
      baseParMap.put("val", basePar.getVal());
      baseParMap.put("id", basePar.getBaseParId());
      baseParList.add(baseParMap);
    });
    return baseParList;
  }

  public void upsert(JsonArray baseParArray) {
    baseParArray.forEach(
      baseParJson -> {
        JsonObject baseParObject = baseParJson.getAsJsonObject();
        BasePar basePar = baseParRepo.findByBaseParId(
          baseParObject
            .get("id")
            .getAsLong());
        basePar.setVal(baseParObject.get("val").getAsString());
        save(basePar);
      }
    );
  }
}
