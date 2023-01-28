package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.DonationRegistration;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.ScholarshipRegistration;
import ch.redanz.redanzCore.model.registration.entities.VolunteerRegistration;
import ch.redanz.redanzCore.model.registration.repository.DonationRegistrationRepo;
import ch.redanz.redanzCore.model.registration.repository.ScholarshipRegistrationRepo;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class DonationRegistrationService {
  private final DonationRegistrationRepo donationRegistrationRepo;
  private final ScholarshipRegistrationRepo scholarshipRegistrationRepo;

  public void saveDonationRegistration(DonationRegistration donationRegistration) {
    donationRegistrationRepo.save(donationRegistration);
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

  public void updateDonationRegistration(Registration registration, JsonArray donationRegistration) {
    Double amount = donationRegistration.get(0).getAsJsonObject().get("amount").getAsDouble();
    DonationRegistration existingDonationRegistration = donationRegistrationRepo.findByRegistration(registration);

    if (existingDonationRegistration != null) {

      // update existing registration
      if (existingDonationRegistration.getAmount() != amount) {
        existingDonationRegistration.setAmount(amount);
        donationRegistrationRepo.save(existingDonationRegistration);
      }

    } else {

      // new host registration
      donationRegistrationRepo.save(
        new DonationRegistration(
          registration,
          amount
        )
      );
    }
  }

  public JsonArray donationRegistrationArray(JsonObject volunteerRegistrationRequest) {
    JsonElement donationRegistration = volunteerRegistrationRequest.get("donationRegistration");
    if (donationRegistration != null && !donationRegistration.isJsonNull()) {
      return donationRegistration.getAsJsonArray();
    }
    return null;
  }

  public void updateDonationRequest(Registration registration, JsonObject request) {
    log.info("request" + request);
    JsonArray donationRegistrationArray = donationRegistrationArray(request);
    log.info("donationRegistrationArray" + donationRegistrationArray);
    if (donationRegistrationArray != null) {

      // update existing
      updateDonationRegistration(registration, donationRegistrationArray);

    } else {

      // delete existing host registration
      donationRegistrationRepo.deleteAllByRegistration(registration);
    }
  }

  public void updateScholarshipRegistration(Registration registration, JsonArray scholarshipRegistration) {
    String intro = scholarshipRegistration.get(0).getAsJsonObject().get("intro").getAsString();
    ScholarshipRegistration existingScholarshipRegistration = scholarshipRegistrationRepo.findByRegistration(registration);

    if (existingScholarshipRegistration != null) {

      // update existing registration
      if (existingScholarshipRegistration.getIntro() != intro) {
        existingScholarshipRegistration.setIntro(intro);
        scholarshipRegistrationRepo.save(existingScholarshipRegistration);
      }

    } else {

      // new host registration
      scholarshipRegistrationRepo.save(
        new ScholarshipRegistration(
          registration,
          intro
        )
      );
    }
  }

  public JsonArray scholarshipRegistrationArray(JsonObject scholarshipRegistrationRequest) {
    JsonElement scholarshipRegistration = scholarshipRegistrationRequest.get("scholarshipRegistration");
    if (scholarshipRegistration != null && !scholarshipRegistration.isJsonNull()) {
      return scholarshipRegistration.getAsJsonArray();
    }
    return null;
  }

  public void updateScholarshipRequest(Registration registration, JsonObject request) {
    log.info("request" + request);
    JsonArray scholarshipRegistrationArray = scholarshipRegistrationArray(request);
    log.info("scholarshipRegistrationArray" + scholarshipRegistrationArray);
    if (scholarshipRegistrationArray != null) {

      // update existing
      updateScholarshipRegistration(registration, scholarshipRegistrationArray);

    } else {

      // delete existing host registration
      scholarshipRegistrationRepo.deleteAllByRegistration(registration);
    }
  }
}
