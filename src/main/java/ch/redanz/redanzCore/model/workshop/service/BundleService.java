package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.BundleTrack;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackBundleRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BundleService {
    private final BundleRepo bundleRepo;
    private final TrackBundleRepo trackBundleRepo;
    public void save(Bundle bundle) {
        bundleRepo.save(bundle);
    }

    public void save(BundleTrack bundleTrack) {
        trackBundleRepo.save(bundleTrack);
    }

    public Bundle findByBundleId(Long bundleId) {
        return bundleRepo.findByBundleId(bundleId);
    }
    public Bundle findByName(String name){return bundleRepo.findByName(name);}
}
