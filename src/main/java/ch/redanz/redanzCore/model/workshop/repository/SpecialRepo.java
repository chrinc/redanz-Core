package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Special;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SpecialRepo extends JpaRepository<Special, Long> {
  Special findByName(String name);
  Special findBySpecialId(Long name);
  boolean existsByName(String name);

}
