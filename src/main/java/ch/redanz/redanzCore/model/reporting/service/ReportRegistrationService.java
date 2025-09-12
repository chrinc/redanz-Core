package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistration;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistrationDetails;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportRegistrationService {
  private final RegistrationService registrationService;
  private final RegistrationMatchingService registrationMatchingService;
  private final WorkflowTransitionService workflowTransitionService;
  private final WorkflowStatusService workflowStatusService;
  private final PaymentService paymentService;

  public List<ResponseRegistrationDetails> getRegistrationDetailsReport(Event event) {
    return getRegistrationsDetails(
      workflowStatusService.findAll(),
      event
    );
  }

  private List<ResponseRegistrationDetails> getRegistrationsDetails(List<WorkflowStatus> workflowStatusList, Event event) {
    List<ResponseRegistrationDetails> registrationDetails = new ArrayList<>();

    registrationService.findAllByEvent(event).forEach(registration -> {
     RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
      WorkflowStatus workflowStatus = workflowStatusService.findById(registration.getWorkflowStatus().getWorkflowStatusId());
      boolean hasPartner = registrationMatching != null && registrationMatching.getRegistration2() != null;
      if (workflowStatusList.contains(workflowStatus)) {
//        log.info("registration.participant: " + registration.getParticipant().getFirstName());
        registrationDetails.add(
          new ResponseRegistrationDetails(
            registration.getParticipant().getPersonId(),
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            registration.getParticipant().getEmail(),
            registration.getBundle().getName(),
            registration.getTrack() == null ? null : registration.getTrack().getName(),
            registration.getDanceRole() == null ? null : registration.getDanceRole().getName(),
            workflowStatus.getName(),
            registrationMatching == null ? null: registrationMatching.getPartnerEmail(),
            registration.getParticipant().getPersonLang().getLanguageKey(),
            hasPartner ? registrationMatching.getRegistration2().getRegistrationId() : null,
            registration.getEvent().getEventId(),
            paymentService.amountDue(registration),
            paymentService.totalAmount(registration)
          )
        );

//        log.info("add registration.participant: " + registration.getParticipant().getFirstName());
      }
    });
    return registrationDetails;
  }

  private WorkflowStatus getLowestWorkflowStatus(List<WorkflowStatus> workflowStatusList) {
    workflowStatusList.sort(Comparator.comparing(WorkflowStatus::getWorkflowStatusId));
    return workflowStatusList.get(0);
  }
}
