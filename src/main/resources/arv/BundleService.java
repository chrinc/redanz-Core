//package ch.redanz.redanzCore.service;
//
//import ch.redanz.redanzCore.model.event.Bundle;
//import ch.redanz.redanzCore.model.profile.Person;
//import ch.redanz.redanzCore.model.workshop.repository.BundleRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class BundleService {
//  private final BundleRepo bundleRepo;
//
//  @Autowired
//  public BundleService(BundleRepo bundleRepo) {
//    this.bundleRepo = bundleRepo;
//  }
//
//  public List<Bundle> getAllBundles() {
//    return bundleRepo.findAll();
//  }
//}
