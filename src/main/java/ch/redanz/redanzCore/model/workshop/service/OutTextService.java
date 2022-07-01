package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.OutText;
import ch.redanz.redanzCore.model.workshop.repository.OutTextRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class OutTextService {
  private final OutTextRepo outTextRepo;
  public void save(OutText outText) {
    outTextRepo.save(outText);
  }

  public OutText getOutTextByKeyAndLangKey(String key, String langKey) {
    return outTextRepo.findAllByOutTextIdOutTextKeyAndOutTextIdOutTextLanguageKey(key, langKey);
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

}
