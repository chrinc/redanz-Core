package ch.redanz.redanzCore.model.service;

import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.model.reporting.ResponseRegistration;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
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

  public List<ResponseRegistration> getAllRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAll());
  }
  public List<ResponseRegistration> getOpenRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllOpen());
  }

  public List<ResponseRegistration> getConfirmingRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllConfirming());
  }

  public List<ResponseRegistration> getDoneRegistrationsReport() {
    return getRegistrations(workflowStatusService.findAllDone());
  }

  private List<ResponseRegistration> getRegistrations(List<WorkflowStatus> workflowStatusList) {
    List<ResponseRegistration> registrations = new ArrayList<>();
    List<Registration> partnerRegistrations = new ArrayList<>();

    registrationService.findAll().forEach(registration -> {
      RegistrationMatching registrationMatching = registrationMatchingService.findByRegistration1(registration).orElse(null);
      boolean hasPartner = registrationMatching != null && registrationMatching.getRegistration2() != null;
      WorkflowStatus workflowStatus = workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration).getWorkflowStatus();
      List<WorkflowStatus> registrationWorkflowStatusList = new ArrayList<>(){{
        add(workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(registration).getWorkflowStatus());
      }};

      if (hasPartner) {
        registrationWorkflowStatusList.add(
          workflowTransitionService.findFirstByRegistrationOrderByTransitionTimestampDesc(
            registrationMatching.getRegistration2()
          ).getWorkflowStatus());
      }

      if (workflowStatusList.contains(getLowestWorkflowStatus(registrationWorkflowStatusList))){

        // make sure leader is left, follower right
        boolean ignoreRegistration =
          hasPartner && (
            registration.getDanceRole().getName().equals(DanceRoleConfig.FOLLOWER.getName()) ||
              registrationMatching.getRegistration2().getDanceRole().getName().equals(DanceRoleConfig.LEADER.getName())
          );

        if (
          hasPartner &&
            registration.getDanceRole().getName().equals(DanceRoleConfig.SWITCH.getName()) &&
            registrationMatching.getRegistration2().getDanceRole().getName().equals(DanceRoleConfig.SWITCH.getName())
        ) {
          partnerRegistrations.add(registrationMatching.getRegistration2());
        }

        // make sure AxB BxA Registrations are eliminated
        if (!ignoreRegistration && !partnerRegistrations.contains(registration)) {
          registrations.add(
            new ResponseRegistration(
              registration.getBundle().getName(),
              registration.getTrack() == null ? null : registration.getTrack().getName(),
              registration.getParticipant().getFirstName(),
              registration.getParticipant().getLastName(),
              registration.getDanceRole() == null ? null : registration.getDanceRole().getName(),
              hasPartner ? registrationMatching.getRegistration2().getParticipant().getFirstName() : null,
              hasPartner ? registrationMatching.getRegistration2().getParticipant().getLastName() : null,
              hasPartner ? registrationMatching.getRegistration2().getDanceRole().getName() : null
            )
          );
        };
      };
    });
    return registrations;
  }

  private WorkflowStatus getLowestWorkflowStatus (List<WorkflowStatus> workflowStatusList) {
    workflowStatusList.sort(Comparator.comparing(WorkflowStatus::getWorkflowStatusId));
    return workflowStatusList.get(0);
  }
}
