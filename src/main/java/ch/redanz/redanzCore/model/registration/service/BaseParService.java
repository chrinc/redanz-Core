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
}
