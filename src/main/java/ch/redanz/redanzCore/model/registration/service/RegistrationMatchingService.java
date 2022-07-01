package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.registration.response.RegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RegistrationMatchingService {

  private final RegistrationMatchingRepo registrationMatchingRepo;

  @Autowired
  public RegistrationMatchingService(
    RegistrationMatchingRepo registrationMatchingRepo
  ) {
    this.registrationMatchingRepo = registrationMatchingRepo;
  }

  public void save(RegistrationMatching registrationMatching) {
    registrationMatchingRepo.save(registrationMatching);
  }

  public Optional<RegistrationMatching> findByRegistration1(Registration registration1) {
    return registrationMatchingRepo.findByRegistration1(registration1);
  }

  public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
    return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
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

  public void doMatch(Registration registration1, Registration registration2) {
    RegistrationMatching registrationMatching1 = findByRegistration1(registration1).get();
    registrationMatching1.setRegistration2(registration2);

    RegistrationMatching registrationMatching2 = findByRegistration1(registration2).get();
    registrationMatching2.setRegistration2(registration1);

    save(registrationMatching1);
    save(registrationMatching2);
  }
}
