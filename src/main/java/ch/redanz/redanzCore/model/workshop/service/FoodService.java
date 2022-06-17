package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Food;
import ch.redanz.redanzCore.model.workshop.repository.FoodRepo;
import ch.redanz.redanzCore.model.workshop.repository.TypeSlotRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FoodService {

  FoodRepo foodRepo;

  public List<Food> findAll() {
    return foodRepo.findAll();
  }
  public Food findByName(String name) {
    return foodRepo.findByName(name);
  }
  public Food findById(Long foodId) {
    return foodRepo.findByFoodId(foodId);
  }
}
