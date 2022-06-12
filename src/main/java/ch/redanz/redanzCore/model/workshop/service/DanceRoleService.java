package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.DanceRole;
import ch.redanz.redanzCore.model.workshop.repository.DanceRoleRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DanceRoleService {
    private final DanceRoleRepo danceRoleRepo;

    public DanceRole findByDanceRoleId(Long danceRoleId) {
        return danceRoleRepo.findByDanceRoleId(danceRoleId);
    }
}
