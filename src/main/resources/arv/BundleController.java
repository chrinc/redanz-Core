//package ch.redanz.redanzCore.arv;
//
//import ch.redanz.redanzCore.model.event.Bundle;
//import ch.redanz.redanzCore.model.workshop.service.BundleService;
//import ch.redanz.redanzCore.model.registration.service.RegistrationService;
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;

//@Slf4j
//@RestController
//@RequestMapping("app/save/bundle")
//@AllArgsConstructor
//public class BundleController {
//  private final BundleService bundleService;
//
//  @GetMapping(path="/all")
//  public List<Bundle> getAllBundles() {
//    log.info("inc, send getBundles: {}.", bundleService.getAllBundles());
//    return bundleService.getAllBundles();
//  }
//}
