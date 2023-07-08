package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import ch.redanz.redanzCore.model.workshop.repository.TrackBundleRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class BundleService {
    private final BundleRepo bundleRepo;
    private final BundleEventRepo bundleEventRepo;
    private final TrackBundleRepo trackBundleRepo;

    public List<Bundle> getAll(){
        return bundleRepo.findAll();
    }
    public void save(Bundle bundle) {
        bundleRepo.save(bundle);
    }
    public void save(BundleTrack bundleTrack) {
        trackBundleRepo.save(bundleTrack);
    }

    public List<Bundle> getAllByEvent(Event event) {
        List<EventBundle> eventBundles;
        List<Bundle> bundles = new ArrayList<>();
        eventBundles = bundleEventRepo.findAllByEvent(event);
        eventBundles.forEach(eventBundle -> {
            bundles.add(eventBundle.getBundle());
        });

        return bundles;
    }
    public boolean existsByName(String name) {
        return bundleRepo.existsByName(name);
    }
    public Bundle findByBundleId(Long bundleId) {
        return bundleRepo.findByBundleId(bundleId);
    }
    public Bundle findByName(String name){return bundleRepo.findByName(name);}
    public Bundle findByInternalId(String internalId) {
        return bundleRepo.findByInternalId(internalId);
    }

    public boolean bundleTrackExists(Bundle bundle, Track track) {
        return trackBundleRepo.existsByBundleAndTrack(bundle, track);
    }
}
