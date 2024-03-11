package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepo extends JpaRepository<Food, Long> {
  Food findByName(String name);
  boolean existsByName(String name);
  Food findByFoodId(Long name);
}
