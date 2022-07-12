package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.repository.SpecialRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SpecialService {

  private final SpecialRepo specialRepo;
  public void save(Special special) {
    specialRepo.save(special);
  }
  public List<Special> findAll() {
    return specialRepo.findAll();
  }
  public Special findByName(String name) {
    return specialRepo.findByName(name);
  }
  public Special findBySpecialId(Long foodId) {
    return specialRepo.findBySpecialId(foodId);
  }
}
