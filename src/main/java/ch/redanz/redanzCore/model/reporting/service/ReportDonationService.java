package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.DonationRegistrationService;
import ch.redanz.redanzCore.model.registration.service.VolunteerService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.reporting.response.ResponseDonation;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportDonationService {private final VolunteerService volunteerService;
  private final OutTextService outTextService;
  private final DonationRegistrationService donationRegistrationService;
  private final WorkflowStatusService workflowStatusService;
  public List<ResponseDonation> getDonationReport(Language language, Event event) {
    List<ResponseDonation> responseDonation = new ArrayList<>();
    donationRegistrationService.allDonationRegistrations(event).forEach(donationRegistration -> {
      Registration registration = donationRegistration.getRegistration();
      if (workflowStatusService.findAllRegular().contains(registration.getWorkflowStatus())) {
        responseDonation.add(
          new ResponseDonation(
            registration.getRegistrationId()
            , registration.getParticipant().getFirstName()
            , registration.getParticipant().getLastName()
            , "Donation"
            , donationRegistration.getAmount()
            , null
            , registration.getWorkflowStatus().getName()
          )
        );
      }
    });
    donationRegistrationService.allScholarshipRegistrations(event).forEach(scholarshipRegistration -> {
      Registration registration = scholarshipRegistration.getRegistration();
      if (workflowStatusService.findAllRegular().contains(registration.getWorkflowStatus())) {
        responseDonation.add(
          new ResponseDonation(
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            "Scholarship",
            null,
            scholarshipRegistration.getIntro(),
            registration.getWorkflowStatus().getName()
          )
        );
      }
    });
    return responseDonation;
  }
}
