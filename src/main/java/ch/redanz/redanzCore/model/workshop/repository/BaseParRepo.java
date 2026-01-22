package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.BasePar;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseParRepo  extends JpaRepository<BasePar, Long> {
  List<BasePar> findAllByEvent(Event event);
  BasePar findByEventAndName(Event event, String name);
  BasePar findByBaseParId(Long id);
  Boolean existsByEventAndName(Event event, String name);
}
