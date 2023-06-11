package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.PrivateClass;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.repository.PrivateClassRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PrivateClassService {
  private final PrivateClassRepo privateClassRepo;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;

  public void save(PrivateClass privateClass) {
    privateClassRepo.save(privateClass);
  }

  public List<PrivateClass> findByEvent(Event event) {
    return privateClassRepo.findAllByEvent(event).isPresent() ?
      privateClassRepo.findAllByEvent(event).get() :
      null;
  }

  public List<PrivateClass> findByEventAndBundle(Event event, Bundle bundle) {
    if (bundle.getSimpleTicket()) {return new ArrayList<>();}
    else { return findByEvent(event);}
  }

  public List<PrivateClassRegistration> findAllByRegistrations(Registration registration) {
    return privateClassRegistrationRepo.findAllByRegistration(registration);
  }

  public PrivateClass findByPrivateClassId(Long privateClassId) {
    return privateClassRepo.findByPrivateClassId(privateClassId);
  }
}
