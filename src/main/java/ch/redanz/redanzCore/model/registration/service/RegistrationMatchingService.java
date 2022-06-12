package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.repository.RegistrationMatchingRepo;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.Registration;
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
    public Optional<RegistrationMatching> findByRegistration2(Registration registration2) {
        return registrationMatchingRepo.findByRegistration1(registration2);
    }
    public boolean existsByRegistration2(Registration registration2) {
        return registrationMatchingRepo.existsByRegistration2(registration2);
    }
    public List<RegistrationMatching> findRegistrationMatchingByRegistration2IsNull() {
        return registrationMatchingRepo.findRegistrationMatchingByRegistration2IsNull();
    }

    public void deleteMatching(RegistrationMatching registrationMatching) {
        registrationMatchingRepo.delete(registrationMatching);
    }
}
