package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.SleepUtil;
import ch.redanz.redanzCore.model.workshop.service.SleepUtilService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SleepUtilRepository extends JpaRepository<SleepUtil, Long> {
  SleepUtil findBySleepUtilId(Long sleepUtilId);
  SleepUtil findByName(String name);
}
