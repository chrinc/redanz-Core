package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistration;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistrationDetails;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
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
  private final EventService eventService;

  public List<ResponseRegistrationDetails> getRegistrationDetailsReport(Event event) {
    return getRegistrationsDetails(
      workflowStatusService.findAll(),
      event
    );
  }
  public List<ResponseRegistration> getAllRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAll());
  }

  public List<ResponseRegistration> getOpenRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllOpen());
  }

  public List<ResponseRegistration> getConfirmingRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllConfirming());
  }

  public List<ResponseRegistration> getSubmittedRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllSubmitted());
  }

  public List<ResponseRegistration> getDoneRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllDone());
  }

  private List<ResponseRegistration> getRegistrations(List<WorkflowStatus> workflowStatusList) {
    List<ResponseRegistration> registrations = new ArrayList<>();
    List<Registration> partnerRegistrations = new ArrayList<>();

    registrationService.findAllCurrentEvent().forEach(registration -> {
      RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
      boolean hasPartner = registrationMatching != null && registrationMatching.getRegistration2() != null;
      List<WorkflowStatus> registrationWorkflowStatusList = new ArrayList<>() {{
        add(workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration).getWorkflowStatus());
      }};
      if (hasPartner) {
        registrationWorkflowStatusList.add(
          workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(
            registrationMatching.getRegistration2()
          ).getWorkflowStatus());
      }

      if (workflowStatusList.contains(getLowestWorkflowStatus(registrationWorkflowStatusList))) {

        boolean ignoreRegistration = false;

        if (hasPartner) {
          boolean rolesMatch = registration.getDanceRole().getName().equals(registrationMatching.getRegistration2().getDanceRole().getName());
          boolean registration1IsFollower = registration.getDanceRole().getName().equals(DanceRoleConfig.FOLLOWER.getName());
          boolean registration2IsFollower = registrationMatching.getRegistration2().getDanceRole().getName().equals(DanceRoleConfig.FOLLOWER.getName());
          boolean registration1IsLeader = registration.getDanceRole().getName().equals(DanceRoleConfig.LEADER.getName());
          boolean registration2IsLeader = registrationMatching.getRegistration2().getDanceRole().getName().equals(DanceRoleConfig.LEADER.getName());

          // make sure leader is left, follower right
          ignoreRegistration =
            registration1IsFollower && !registration2IsFollower
              || (!registration1IsLeader && registration2IsLeader)
          ;

          if (rolesMatch) {
            partnerRegistrations.add(registrationMatching.getRegistration2());
          }
        }

        // make sure AxB BxA Registrations are eliminated
        if (!ignoreRegistration && !partnerRegistrations.contains(registration)) {
          registrations.add(
            new ResponseRegistration(
              registration.getParticipant().getUser().getUserId(),
              registration.getBundle().getName(),
              registration.getTrack() == null ? null : registration.getTrack().getName(),
              registration.getParticipant().getFirstName(),
              registration.getParticipant().getLastName(),
              registration.getDanceRole() == null ? null : registration.getDanceRole().getName(),
              hasPartner ? registrationMatching.getRegistration2().getParticipant().getUser().getUserId() : null,
              hasPartner ? registrationMatching.getRegistration2().getParticipant().getFirstName() : null,
              hasPartner ? registrationMatching.getRegistration2().getParticipant().getLastName() : null,
              hasPartner ? registrationMatching.getRegistration2().getDanceRole().getName() : null,
              registration.getEvent().getEventId()
            )
          );
        }
        ;
      }
      ;
    });
    return registrations;
  }

  private List<ResponseRegistrationDetails> getRegistrationsDetails(List<WorkflowStatus> workflowStatusList, Event event) {
    List<ResponseRegistrationDetails> registrationDetails = new ArrayList<>();

    registrationService.findAllByEvent(event).forEach(registration -> {
      RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
      WorkflowStatus workflowStatus = workflowStatusService.findById(registration.getWorkflowStatus().getWorkflowStatusId());
      boolean hasPartner = registrationMatching != null && registrationMatching.getRegistration2() != null;
      if (workflowStatusList.contains(workflowStatus)) {
        registrationDetails.add(
          new ResponseRegistrationDetails(
            registration.getParticipant().getUser().getUserId(),
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            registration.getParticipant().getUser().getEmail(),
            registration.getBundle().getName(),
            registration.getTrack() == null ? null : registration.getTrack().getName(),
            registration.getDanceRole() == null ? null : registration.getDanceRole().getName(),
            workflowStatus.getName(),
            registrationMatching == null ? null: registrationMatching.getPartnerEmail(),
            hasPartner ? registrationMatching.getRegistration2().getRegistrationId() : null,
            registration.getEvent().getEventId()
          )
        );
      }
    });
    return registrationDetails;
  }

  private WorkflowStatus getLowestWorkflowStatus(List<WorkflowStatus> workflowStatusList) {
    workflowStatusList.sort(Comparator.comparing(WorkflowStatus::getWorkflowStatusId));
    return workflowStatusList.get(0);
  }
}
