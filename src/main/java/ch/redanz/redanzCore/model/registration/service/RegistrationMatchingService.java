package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationMatchingService {

  private final RegistrationMatchingRepo registrationMatchingRepo;
  private final WorkflowStatusService workflowStatusService;
  private final EventService eventService;

  public void save(RegistrationMatching registrationMatching) {
    registrationMatchingRepo.save(registrationMatching);
  }

  public Optional<RegistrationMatching> findByRegistration1(Registration registration1) {
    return registrationMatchingRepo.findByRegistration1(registration1);
  }

  public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
  }

  public List<RegistrationMatching> findRegistration2ISNullSubmittedCurrent(){
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNullAndRegistration1WorkflowStatusAndRegistration1Event(
      workflowStatusService.getSubmitted(),
      eventService.getCurrentEvent()
    );
  }

  public void setRegistrationMatching(Registration registration, RegistrationRequest request) {
    if (request.getTrackId() != null && registration.getTrack().getPartnerRequired()) {
      RegistrationMatching registrationMatching
        = new RegistrationMatching(registration);

      if (request.getPartnerEmail() != null) {
        registrationMatching.setPartnerEmail(String.valueOf(request.getPartnerEmail()));
      }
      save(registrationMatching);
    }
  }

  void cleanupMatchingRequest(Registration registration, boolean totalCleanup) {
    if (registrationMatchingRepo.findByRegistration1(registration).isPresent()) {

      if (totalCleanup) {
        registrationMatchingRepo.deleteAllByRegistration1(registration);
      } else {
        RegistrationMatching registration1Matching = registrationMatchingRepo.findByRegistration1(registration).get();
        registration1Matching.setRegistration2(null);
        registration1Matching.setPartnerEmail(null);
        save(registration1Matching);

      }
    }

    if (registrationMatchingRepo.findByRegistration2(registration).isPresent()) {
      RegistrationMatching registration2Matching = registrationMatchingRepo.findByRegistration2(registration).get();
      registration2Matching.setRegistration2(null);
//      registration2Matching.setPartnerEmail(null);
      save(registration2Matching);
    }
  }

  public void updateMatchingRequest(Registration registration, RegistrationRequest request) {
    if (request.getTrackId() != null && registration.getTrack().getPartnerRequired()) {
      RegistrationMatching registrationMatching
        = registrationMatchingRepo.findByRegistration1(registration).isPresent() ?
          registrationMatchingRepo.findByRegistration1(registration).get() :
          new RegistrationMatching(registration);

      if (request.getPartnerEmail() != null) {
        registrationMatching.setPartnerEmail(String.valueOf(request.getPartnerEmail()));
        save(registrationMatching);
      } else {
        cleanupMatchingRequest(registration, false);
      }
    } else {
      cleanupMatchingRequest(registration, true);
    }
  }

  public void doMatch(Registration registration1, Registration registration2) {
    if (registrationMatchingRepo.findByRegistration2(registration1).isPresent()) {
      RegistrationMatching oldMatching1 = registrationMatchingRepo.findByRegistration2(registration1).get();
      oldMatching1.setRegistration2(null);
      save(oldMatching1);
    }
    if (registrationMatchingRepo.findByRegistration2(registration2).isPresent()) {
      RegistrationMatching oldMatching2 = registrationMatchingRepo.findByRegistration2(registration2).get();
      oldMatching2.setRegistration2(null);
      save(oldMatching2);
    }

    RegistrationMatching registrationMatching1 = findByRegistration1(registration1).get();
    registrationMatching1.setRegistration2(registration2);

    RegistrationMatching registrationMatching2 = findByRegistration1(registration2).get();
    registrationMatching2.setRegistration2(registration1);

    save(registrationMatching1);
    save(registrationMatching2);
  }
}
