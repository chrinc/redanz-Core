package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Bundle;
import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BundleService {
    private final BundleRepo bundleRepo;

    @Autowired
    public BundleService(BundleRepo bundleRepo) {
        this.bundleRepo = bundleRepo;
    }

    public Bundle findByBundleId(Long bundleId) {
        return bundleRepo.findByBundleId(bundleId);
    }
}
