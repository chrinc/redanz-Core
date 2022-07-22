package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
@AllArgsConstructor
@EnableAsync
public class EODReleaseJob {
  private final RegistrationMatchingService registrationMatchingService;
  private final WorkflowTransitionService workflowTransitionService;
  private final WorkflowStatusService workflowStatusService;
  private final RegistrationService registrationService;
  private final OutTextService outTextService;
  private final EventService eventService;
  private final RegistrationEmailService registrationEmailService;
  private List<Registration> releasedRegistrations;

  @Autowired
  Configuration mailConfig;

//  @Scheduled(cron = "0 50 15 * * MON-SUN")
//  @Scheduled(cron = "0 0/2 * * * *")

  @Scheduled(cron = "${cron.matching.scheduler.value.release}")
  public void runRelease() {
      log.info("Job: runRelease");
      registrationService.getAllSubmittedRegistrations().forEach(registration -> {
//        List<Registration> releasedRegistrations = new ArrayList<>();

        if (isRelease(registration) && !releasedRegistrations.contains(registration)) {
          try {

            // release partner first
            if (registrationMatchingService.findByRegistration1(registration).isPresent()) {
              Registration partnerRegistration = registrationMatchingService.findByRegistration1(registration).get().getRegistration2();
              releaseToConfirming(partnerRegistration);
              log.info("send email to: {}", partnerRegistration.getParticipant().getFirstName());
              registrationEmailService.sendEmailConfirmation(partnerRegistration , registrationEmailService.findByRegistration(partnerRegistration));
              releasedRegistrations.add(partnerRegistration);
            }

            // release registration
            releaseToConfirming(registration);
            log.info("send email to: {}", registration.getParticipant().getFirstName());
            registrationEmailService.sendEmailConfirmation(registration, registrationEmailService.findByRegistration(registration));
            releasedRegistrations.add(registration);
          } catch (IOException | TemplateException e) {
            e.printStackTrace();
          }
        }
      });
      registrationService.updateSoldOut();
    }

  private boolean isRelease(Registration registration) {
    return
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
      registrationService.countConfirmingAndDone() < eventService.getCurrentEvent().getCapacity() &&
      registrationService.countBundlesConfirmingAndDone(
        registration.getBundle()
      ) < registration.getBundle().getCapacity() &&
      registrationService.countTracksConfirmingAndDone(
        registration.getTrack()
      ) < (registration.getTrack() == null ? 99 : registration.getTrack().getCapacity())
    ;
  }

  private void releaseToConfirming(Registration registration) {
    workflowTransitionService.setWorkflowStatus(
      registration,
      workflowStatusService.getConfirming()
    );
    registrationService.updateSoldOut();
  }
}
