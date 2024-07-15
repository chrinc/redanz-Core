package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.DanceRole;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class DanceRoleService {
  private final DanceRoleRepo danceRoleRepo;

  public boolean existsByName(String name) {
    return danceRoleRepo.existsByName(name);
  }
  public void save(DanceRole danceRole) {
    danceRoleRepo.save(danceRole);
  }
  public DanceRole findByInternalId(String internalId) {
    return danceRoleRepo.findByInternalId(internalId);
  }

  public DanceRole findByDanceRoleId(Long danceRoleId) {
    return danceRoleRepo.findByDanceRoleId(danceRoleId);
  }

  public DanceRole findByName(String name) {
    return danceRoleRepo.findByName(name);
  }

  public DanceRole getSwitchDanceRole(){
    return findByName(DanceRoleConfig.SWITCH.getName());
  }

  public List<DanceRole> all() {
    return danceRoleRepo.findAll();
  }

  public List<Map<String, String>> getDanceRolesMap() {
    List<Map<String, String>> danceRoles = new ArrayList<>();
    danceRoleRepo.findAll().forEach(danceRole -> {
      danceRoles.add(danceRole.dataMap());
    });
    return danceRoles;
  }
}
