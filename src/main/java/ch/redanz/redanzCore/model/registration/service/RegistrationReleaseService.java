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
  private final RegistrationEmailService registrationEmailService;
  private final WorkflowStatusService workflowStatusService;
  private final PaymentService paymentService;

  public boolean doRelease(Registration registration){
    if (isRelease(registration)) {
      try {
        // release partner first
        if (
          registrationMatchingService.findByRegistration1(registration).isPresent()
          && registrationMatchingService.findByRegistration1(registration).get().getRegistration2() != null
          && isRelease(registrationMatchingService.findByRegistration1(registration).get().getRegistration2())
        ) {
          Registration partnerRegistration = registrationMatchingService.findByRegistration1(registration).get().getRegistration2();
          registrationService.releaseToConfirming(partnerRegistration);
          registrationEmailService.sendEmailConfirmation(partnerRegistration , registrationEmailService.findByRegistration(partnerRegistration), paymentService.getPaymentDetails(partnerRegistration));
        }

        // release registration
        registrationService.releaseToConfirming(registration);
        registrationEmailService.sendEmailConfirmation(registration, registrationEmailService.findByRegistration(registration), paymentService.getPaymentDetails(registration));
        return true;
      } catch (IOException | TemplateException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  public void doRelease(Event event) {
    registrationService.getAllSubmittedRegistrations(event).forEach(registration -> {
      doRelease(registration);
    });
  }

  private boolean isRelease(Registration registration) {
    return
      registration.getWorkflowStatus().getWorkflowStatusId().equals(workflowStatusService.getSubmitted().getWorkflowStatusId())
        && isMatchingOK(registration)
        && isEventCapacityOK(registration)
      ;
  }

  private boolean isMatchingOK (Registration registration) {
    return registration.getIsRelease();
  }

  private boolean isEventCapacityOK (Registration registration) {
    return
      registrationService.countConfirmingAndDone(
        registration.getEvent()
      ) < registration.getEvent().getCapacity();
  }
}
