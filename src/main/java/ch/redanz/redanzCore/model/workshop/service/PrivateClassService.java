package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.PrivateClassRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.SpecialRegistration;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PrivateClassService {
  private final PrivateClassRepo privateClassRepo;
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final OutTextService outTextService;
  public boolean existsByName(String name) {
    return privateClassRepo.existsByName(name);
  }
  public PrivateClass findByName(String name) {
    return privateClassRepo.findByName(name);
  }
  public void save(PrivateClass privateClass) {
    privateClassRepo.save(privateClass);
  }

  public String getReportPrivates(Registration registration, Language language) {
    AtomicReference<String> privates = new AtomicReference<>();
    privateClassRegistrationRepo.findAllByRegistration(registration).forEach(privateRegistration -> {
      String privateOutText = outTextService.getOutTextByKeyAndLangKey(privateRegistration.getPrivateClass().getDescription(), language.getLanguageKey()).getOutText();
      if (privates.get() == null)
      privates.set(privateOutText);
      else {
        privates.set(privates.get() + ", " + privateOutText);
      }
    });
    return privates.get() == null ? "" : privates.toString();
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

  public Set<Registration> getRegistrationsByEvent(Event event) {
    List<PrivateClassRegistration> privateRegistrations = privateClassRegistrationRepo.findAllByRegistrationEventAndRegistrationActive(event, true);

    Set<Registration> registrations = privateRegistrations.stream()
      .map(PrivateClassRegistration::getRegistration)
      .filter(Registration::getActive)
      .collect(Collectors.toSet());

    return registrations;
  }
}
