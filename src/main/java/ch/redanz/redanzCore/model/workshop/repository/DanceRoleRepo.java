package ch.redanz.redanzCore.model.workshop.repository;

import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DanceRoleRepo extends JpaRepository<DanceRole, Long> {
  DanceRole findByName(String name);

  DanceRole findByDanceRoleId(Long danceRoleId);
}
