package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.SleepUtil;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepUtilRepository extends JpaRepository<SleepUtil, Long> {
  SleepUtil findBySleepUtilId(Long sleepUtilId);
  boolean existsByName(String name);
  SleepUtil findByName(String name);
}
