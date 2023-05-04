package ch.redanz.redanzCore.model.registration.repository;

import ch.redanz.redanzCore.model.registration.entities.BasePar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BaseParRepo  extends JpaRepository<BasePar, Long> {
  Optional<BasePar> findAllByBaseParKey(String baseParKey);
}
