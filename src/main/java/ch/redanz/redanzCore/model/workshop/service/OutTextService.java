package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

  public boolean hasLabel(JsonArray labelArray, String key) {
    AtomicBoolean hasLabel = new AtomicBoolean(false);
    labelArray.forEach(jsonElement -> {
      if (!jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        if (jsonObject.has("key")) {
          if (jsonObject.get("key").getAsString().equals(key)) {
            hasLabel.set(true);
          }
        }
      }
    });
    return hasLabel.get();
  }

  public String updateLabelArray(JsonArray labelArray, String key) {
    String newLabelKey = "LABEL-USER-GEN-" + System.currentTimeMillis();
    boolean hasLabel = hasLabel(labelArray, key);
    if (!hasLabel(labelArray, key)) {
      return null;
    }

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
    saveAll(outTexts);
    return outTextKey;
  }

  public String newFrontLoginOutText(Map<Language, String> outTextMap) {
    String newLabelKey = "LABEL-AUTO-GEN-" + System.currentTimeMillis();
    saveAll(outTextMap.entrySet().stream()
      .map(entry -> new OutText(
        new OutTextId(newLabelKey, entry.getKey().getLanguageKey()),
        entry.getValue(),
        "FRONT_LOGIN"
      ))
      .collect(Collectors.toList()));
    return newLabelKey;
  }

  public void delete(String key) {
    outTextRepo.findAllByOutTextIdOutTextKey(key).forEach(outText -> {
      outTextRepo.delete(outText);
    });
  }

  public String clone(String key) {
    if (key == null) {
      return null;
    }

    String newKey = "LABEL-USER-GEN-" + System.currentTimeMillis();
    outTextRepo.findAllByOutTextIdOutTextKey(key).forEach(baseOutText -> {
      OutText newOutText = new OutText(
        new OutTextId(
          newKey, baseOutText.getOutTextId().getOutTextLanguageKey()
        ),
        baseOutText.getOutText(),
        baseOutText.getType()
      );
      save(newOutText);
    });
    return newKey;
  }

}
