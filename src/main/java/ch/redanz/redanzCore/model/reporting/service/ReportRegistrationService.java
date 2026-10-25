package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.service.FieldPropertyService;
import ch.redanz.redanzCore.model.profile.service.PersonService;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistration;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistrationDetails;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReportRegistrationService {
  private final RegistrationService registrationService;
  private final RegistrationMatchingService registrationMatchingService;
  private final OutTextService outTextService;
  private final WorkflowStatusService workflowStatusService;
  private final PaymentService paymentService;
  private final PersonService personService;
  private final FieldPropertyService fieldPropertyService;

  public List<ResponseRegistrationDetails> getRegistrationDetailsReport(Event event) {
    return getRegistrationsDetails(
      workflowStatusService.findAll(),
      event
    );
  }

  public List<String> personRegistrationFieldProperties() {
    List<String> fieldList = new ArrayList<>();
    fieldList.add("registrationId");
    
    fieldPropertyService.displayedColumns().forEach(property -> {
      fieldList.add(property);
    });
    fieldList.add("workflowStatus");
    fieldList.add("bundle");
    fieldList.add("track");
    fieldList.add("email");
    fieldList.add("mobile");
    fieldList.add("role");

    return fieldList;
  }

  private List<ResponseRegistrationDetails> getRegistrationsDetails(List<WorkflowStatus> workflowStatusList, Event event) {
    List<ResponseRegistrationDetails> registrationDetails = new ArrayList<>();

    // loaded once for all registrations of the event (not per registration: slow with many registrations)
    List<Registration> registrations = registrationService.findAllByEvent(event);
    Map<Long, RegistrationMatching> registrationMatchings = registrationMatchingService.findAllByRegistration1Event(event);
    Map<Long, PaymentService.Amounts> amounts = paymentService.amountsByRegistration(event, registrations);
    Map<Long, String> workflowStatusMaps = new HashMap<>();
    Map<Long, WorkflowStatus> workflowStatuses = new HashMap<>();
    workflowStatusList.forEach(workflowStatus -> workflowStatuses.put(workflowStatus.getWorkflowStatusId(), workflowStatus));

    registrations.forEach(registration -> {
      RegistrationMatching registrationMatching = registrationMatchings.get(registration.getRegistrationId());
      WorkflowStatus workflowStatus = workflowStatuses.get(registration.getWorkflowStatus().getWorkflowStatusId());
      boolean hasPartner = registrationMatching != null && registrationMatching.getRegistration2() != null;
      if (workflowStatus != null) {
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
            workflowStatusMaps.computeIfAbsent(workflowStatus.getWorkflowStatusId(), id -> getWorkflowStatusMap(registration.getWorkflowStatus())),
            registrationMatching == null ? null: registrationMatching.getPartnerEmail(),
            registration.getParticipant().getPersonLang().getLanguageKey(),
            hasPartner ? registrationMatching.getRegistration2().getRegistrationId() : null,
            registration.getEvent().getEventId(),
            amounts.get(registration.getRegistrationId()).amountDue(),
            amounts.get(registration.getRegistrationId()).totalAmount()
          )
        );
      }
    });
    return registrationDetails;
  }

  private String getWorkflowStatusMap(WorkflowStatus workflowStatus) {
    List<Map<String, String>> outTextMap = outTextService.getOutTextMapByKey(workflowStatus.getLabel());
    outTextMap.get(0).put("code", workflowStatus.getName().toUpperCase());
    return outTextMap.toString();
  }


}
