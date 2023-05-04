package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.SpecialRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialService {
  private final SpecialRepo specialRepo;
  public void save(Special special) {
    specialRepo.save(special);
  }

  public List<Special> findByEvent(Event event) {
    specialRepo.findAllByEvent(event).forEach(special -> {
      log.info("Event special name: " + special.getName());
    });
    return specialRepo.findAllByEvent(event);
  }
  public List<Special> findByBundle(Bundle bundle) {

    return specialRepo.findAllByBundle(bundle);
  }

  public List<Special> findByEventOrBundle(Event event, Bundle bundle) {
    List<Special> allSpecials;
    allSpecials = findByEvent(event);

    allSpecials.addAll(findByBundle(bundle));
    return allSpecials;
  }
  public Special findByName(String name) {
    return specialRepo.findByName(name);
  }
  public Special findBySpecialId(Long foodId) {
    return specialRepo.findBySpecialId(foodId);
  }
}
