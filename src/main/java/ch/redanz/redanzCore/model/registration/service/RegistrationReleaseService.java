package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class RegistrationReleaseService {
  private final RegistrationMatchingService registrationMatchingService;
  private final RegistrationService registrationService;
  private final EventService eventService;
  private final RegistrationEmailService registrationEmailService;
  private final WorkflowStatusService workflowStatusService;

  public void doRelease(Registration registration){
    if (
         isRelease(registration)
    ) {
      try {

        // release partner first
        if (
          registrationMatchingService.findByRegistration1(registration).isPresent()
        ) {
          Registration partnerRegistration = registrationMatchingService.findByRegistration1(registration).get().getRegistration2();
          registrationService.releaseToConfirming(partnerRegistration);
          registrationEmailService.sendEmailConfirmation(partnerRegistration , registrationEmailService.findByRegistration(partnerRegistration));
          // releasedRegistrations.add(partnerRegistration);
        }

        // release registration
        registrationService.releaseToConfirming(registration);
        registrationEmailService.sendEmailConfirmation(registration, registrationEmailService.findByRegistration(registration));

//        releasedRegistrations.add(registration);
      } catch (IOException | TemplateException e) {
        e.printStackTrace();
      }
    } else {
    }
  }

  public void doRelease(Event event) {
    registrationService.getAllSubmittedRegistrations(event).forEach(registration -> {
      doRelease(registration);
    });
  }

  private boolean isRelease(Registration registration) {
    return
      registration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId()) &&
      isMatchingOK(registration) &&
        isCapacityOK(registration)
      ;
  }

  private boolean isMatchingOK (Registration registration) {
    if (registrationMatchingService.findByRegistration1(registration).isPresent()) {
      return registrationMatchingService.findByRegistration1(registration).get().getRegistration2() != null;
    } else return true;
  }

  private boolean isCapacityOK (Registration registration) {
    return
      registrationService.countConfirmingAndDone(
        registration.getEvent()
      ) < registration.getEvent().getCapacity();
  }
}
