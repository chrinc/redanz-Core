package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BundleRepo extends JpaRepository<Bundle, Long> {
  Bundle findByName(String name);

  Bundle findByBundleId(Long bundleId);

}
