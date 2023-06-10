package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.repository.BaseParRepo;
import ch.redanz.redanzCore.model.registration.repository.DiscountRegistrationRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BaseParService {
  private final BaseParRepo baseParRepo;
  public boolean doAutoMatch() {
    return baseParRepo.findAllByBaseParKey("doAutoMatch").isPresent() ?
      baseParRepo.findAllByBaseParKey("doAutoMatch").get().getBoolValue() :
      false; // default: false
  }

  public boolean doAutoRelease(){
    return baseParRepo.findAllByBaseParKey("doAutoRelease").isPresent() ?
    baseParRepo.findAllByBaseParKey("doAutoRelease").get().getBoolValue() :
    false; // default: false
  }

  public boolean testMailOnly(){
    return baseParRepo.findAllByBaseParKey("testMailOnly").isPresent() ?
    baseParRepo.findAllByBaseParKey("testMailOnly").get().getBoolValue() :
    true; // default: true
  }

  public boolean doEODCancel(){
    return baseParRepo.findAllByBaseParKey("doEODCancel").isPresent() ?
    baseParRepo.findAllByBaseParKey("doEODCancel").get().getBoolValue() :
    true; // default: true
  }

  public boolean doEODMatching(){
    return baseParRepo.findAllByBaseParKey("doEODMatching").isPresent() ?
    baseParRepo.findAllByBaseParKey("doEODMatching").get().getBoolValue() :
    true; // default: true
  }

  public boolean doEODRelease(){
    return baseParRepo.findAllByBaseParKey("doEODRelease").isPresent() ?
    baseParRepo.findAllByBaseParKey("doEODRelease").get().getBoolValue() :
    true; // default: true
  }

  public boolean doEODReminder(){
    return baseParRepo.findAllByBaseParKey("doEODReminder").isPresent() ?
    baseParRepo.findAllByBaseParKey("doEODReminder").get().getBoolValue() :
    true; // default: true
  }
}
