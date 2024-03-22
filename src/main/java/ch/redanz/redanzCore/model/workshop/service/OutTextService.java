package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.entities.OutTextId;
import ch.redanz.redanzCore.model.workshop.repository.OutTextRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
@Slf4j
public class OutTextService {
  private final OutTextRepo outTextRepo;

  public void save(OutText outText) {
    outTextRepo.save(outText);
  }

  public void saveAll(List<OutText> outTexts) {
    outTextRepo.saveAll(outTexts);
  }

  public boolean outTextExists(String key, String langKey) {
    return outTextRepo.findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(key, langKey).isPresent();

  }

  public OutText getOutTextByKeyAndLangKey(String key, String langKey) {
    return outTextRepo.findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(key, langKey).get();
  }

  public HashMap<String, String> getOutTextByType(List<String> types) {
    HashMap<String, String> outTextMap = new HashMap<>();

    types.forEach(type -> {
      outTextRepo.findAllByType(type).forEach(
        outText -> outTextMap.put(
          outText.getOutTextId().getOutTextKey() + "." + outText.getOutTextId().getOutTextLanguageKey(),
          outText.getOutText()
        ));
    });

    return outTextMap;
  }

  public List<Map<String, String>> getOutTextMapByKey(String key) {
    List<Map<String, String>> outTextList = new ArrayList<>();


    Map<String, String> labelMap = new HashMap<>();
    labelMap.put("key", key);
    outTextRepo.findAllByOutTextIdOutTextKey(key).forEach(outText -> {
      labelMap.put(outText.getOutTextId().getOutTextLanguageKey(), outText.getOutText());
    });
    outTextList.add(labelMap);
    return outTextList;
  }

  public List<Map<String, String>> getOutTextMapByType(List<String> types) {
    List<Map<String, String>> outTextList = new ArrayList<>();

    types.forEach(type -> {
      outTextRepo.findAllByOutTextIdOutTextLanguageKeyAndType("EN", type).forEach(
        outTextBundle -> {
          Map<String, String> labelMap = new HashMap<>();
          labelMap.put("key", outTextBundle.getOutTextId().getOutTextKey());
          outTextRepo.findAllByOutTextIdOutTextKey(outTextBundle.getOutTextId().getOutTextKey()).forEach(outText -> {
            labelMap.put(outText.getOutTextId().getOutTextLanguageKey(), outText.getOutText());
          });
          outTextList.add(labelMap);
        });

    });
    return outTextList;
  }

  public JsonObject getLabelMap(JsonArray labelArray, String key) {
    AtomicReference<JsonObject> labelMap = new AtomicReference<>();
    labelArray.forEach(label -> {
      if (label.getAsJsonObject().get("key").getAsString().equals(key))
        labelMap.set(label.getAsJsonObject());
    });
    return labelMap.get();
  }

  public String updateLabelArray(JsonArray labelArray, String key) {
    String newLabelKey = "LABEL-USER-GEN-" + System.currentTimeMillis();
    JsonObject labelMap = getLabelMap(labelArray, key);
    String outTextKey = key;
    List<OutText> outTexts = new ArrayList<>();

    if (outTextExists(outTextKey, "EN")) {
      OutText outTextEn = getOutTextByKeyAndLangKey(outTextKey, "EN");
      outTextEn.setOutText(labelMap.get("EN").getAsString());
      outTexts.add(outTextEn);
      save(outTextEn);
      OutText outTextGe = getOutTextByKeyAndLangKey(outTextKey, "GE");
      outTextGe.setOutText(labelMap.get("GE").getAsString());
      outTexts.add(outTextGe);
    } else {
      log.info("create new label");
      log.info("create new label, key: {}", newLabelKey);
      outTextKey = newLabelKey;
      outTexts.add(
        new OutText(
          new OutTextId(
            newLabelKey, "EN"
          ),
          labelMap.get("EN").getAsString(),
          "FRONT_LOGIN"
        )
      );
      outTexts.add(
        new OutText(
          new OutTextId(
            newLabelKey, "GE"
          ),
          labelMap.get("GE").getAsString(),
          "FRONT_LOGIN"
        )
      );
    }
    log.info("create new label, bfr save:");
    saveAll(outTexts);

    return outTextKey;
  }
}
//  }  public String updateLabelArray(JsonArray labelArray) {
//    log.info(labelArray.toString());
//    String newLabelKey = "LABEL-USER-GEN-" + System.currentTimeMillis();
//    labelArray.forEach(label -> {
//      JsonObject labelMap = label.getAsJsonObject();
//      String outTextKey = labelMap.get("key").getAsString();
//      List<OutText> outTexts = new ArrayList<>();
//
//      if (outTextExists(outTextKey, "EN")) {
//        OutText outTextEn = getOutTextByKeyAndLangKey(outTextKey, "EN");
//        outTextEn.setOutText(labelMap.get("EN").getAsString());
//        outTexts.add(outTextEn);
//        save(outTextEn);
//        OutText outTextGe = getOutTextByKeyAndLangKey(outTextKey, "GE");
//        outTextGe.setOutText(labelMap.get("GE").getAsString());
//        outTexts.add(outTextGe);
//      } else {
//        log.info("create new label");
//        log.info("create new label, key: {}", newLabelKey);
//        outTextKey = newLabelKey;
//        outTexts.add(
//          new OutText(
//            new OutTextId(
//              newLabelKey,"EN"
//            ),
//            labelMap.get("EN").getAsString(),
//            "FRONT_LOGIN"
//          )
//        );
//        outTexts.add(
//          new OutText(
//            new OutTextId(
//              newLabelKey,"GE"
//            ),
//            labelMap.get("GE").getAsString(),
//            "FRONT_LOGIN"
//          )
//        );
//      }
//      log.info("create new label, bfr save:");
//      saveAll(outTexts);
//    });
//
//      return outTextKey;
//  }
