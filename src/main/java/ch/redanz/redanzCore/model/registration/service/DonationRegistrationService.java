package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.ScholarshipRegistrationRepo;
import com.google.gson.JsonArray;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class DonationRegistrationService {
  private final DonationRegistrationRepo donationRegistrationRepo;
  private final ScholarshipRegistrationRepo scholarshipRegistrationRepo;

  public void saveScholarshipRegistration(ScholarshipRegistration scholarshipRegistration) {
    scholarshipRegistrationRepo.save(scholarshipRegistration);
  }
  public void saveDonationRegistration(DonationRegistration donationRegistration) {
    donationRegistrationRepo.save(donationRegistration);
  }

  public void saveScholarishpRegistration(Registration registration, JsonArray scholarshipRegistration) {
    saveScholarshipRegistration(
      new ScholarshipRegistration(
        registration,
        scholarshipRegistration.get(0).getAsJsonObject().get("intro").getAsString()
      )
    );
  }

  public void saveDonationRegistration(Registration registration, JsonArray donationRegistration) {
    saveDonationRegistration(
      new DonationRegistration(
        registration,
        donationRegistration.get(0).getAsJsonObject().get("amount").getAsInt()
      )
    );
  }

  public Map<String, String> getScholarshipRegistration(Registration registration) {
    Map<String, String> scholarshipRegistrationMap = new HashMap<>();
    ScholarshipRegistration scholarshipRegistration = scholarshipRegistrationRepo.findByRegistration(registration);
    if (scholarshipRegistration != null) {
      scholarshipRegistrationMap.put(
        "intro"
        , scholarshipRegistration.getIntro()
      );
      return scholarshipRegistrationMap;
    } else {
      return null;
    }
  }

  public Map<String, String> getDonationRegistration(Registration registration) {
    Map<String, String> donationRegistrationMap = new HashMap<>();

    DonationRegistration donationRegistration = donationRegistrationRepo.findByRegistration(registration);
    if (donationRegistration != null) {

      donationRegistrationMap.put(
        "value"
        , String.valueOf(donationRegistration.getAmount())
      );
      return donationRegistrationMap;
    } else {
      return null;
    }
  }
}
