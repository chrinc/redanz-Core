package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DanceRoleService {
  private final DanceRoleRepo danceRoleRepo;

  public void save(DanceRole danceRole) {
    danceRoleRepo.save(danceRole);
  }

  public DanceRole findByDanceRoleId(Long danceRoleId) {
    return danceRoleRepo.findByDanceRoleId(danceRoleId);
  }

  public DanceRole findByName(String name) {
    return danceRoleRepo.findByName(name);
  }
}
