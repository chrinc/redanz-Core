package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.SpecialRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class SpecialService {
  private final SpecialRepo specialRepo;
  public void save(Special special) {
    specialRepo.save(special);
  }

  public Set<Special> findByEvent(Event event) {
    return specialRepo.findAllByEvent(event).orElse(null);
  }
  public Set<Special> findByBundle(Bundle bundle) {
    return specialRepo.findAllByBundle(bundle).orElse(null);
  }

  public boolean existsByName(String name) {
    return specialRepo.existsByName(name);
  }
  public Set<Special> findByEventOrBundle(Event event, Bundle bundle) {
    Set<Special> allSpecials;
    allSpecials = findByEvent(event);
    allSpecials.forEach(special -> {
      log.info("event");
      log.info(special.getName());
    });
    allSpecials.addAll(findByBundle(bundle));
    allSpecials.forEach(special -> {
      log.info("on bundle");
      log.info(special.getName());
    });
    return allSpecials;
  }
  public Special findByName(String name) {
    return specialRepo.findByName(name);
  }
  public Special findBySpecialId(Long foodId) {
    return specialRepo.findBySpecialId(foodId);
  }
}
