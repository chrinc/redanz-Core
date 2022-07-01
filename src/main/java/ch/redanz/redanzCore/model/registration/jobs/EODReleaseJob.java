package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

  @Autowired
  Configuration mailConfig;

  //  @Scheduled(cron = "${cron.matching.scheduler.value}")
//  @Scheduled(cron = "0 50 15 * * MON-SUN")
  @Scheduled(cron = "0 0/15 * * * *")
  public void runRelease() throws InterruptedException {
    registrationService.getAllSubmittedRegistrations().forEach(registration -> {

      RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
      if (isRelease(registration)) {
        releaseToConfirming(registration);
        try {
          registrationEmailService.sendEmailConfirmation(registration, registrationEmail);
        } catch (IOException | TemplateException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private boolean isRelease(Registration registration) {
    return
      isMatchingOK(registration) &&
      isCapacityOK(registration)
      ;
  }

  private boolean isMatchingOK (Registration registration) {
    if (registrationMatchingService.findByRegistration1(registration).isPresent()) {
      if (registrationMatchingService.findByRegistration1(registration).get().getRegistration2() != null) {
        return true;
      } else
        return false;
    } else return true;
  }
  private boolean isCapacityOK (Registration registration) {
    return
      registrationService.countReleasedAndDone() < eventService.getCurrentEvent().getCapacity() &&
      registrationService.countBundlesReleasedAndDone(
        registration.getBundle()
      ) < registration.getBundle().getCapacity() &&
      registrationService.countTracksReleasedAndDone(
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
