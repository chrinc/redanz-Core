package ch.redanz.redanzCore.model.registration.jobs;

import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.registration.WorkflowTransition;
import ch.redanz.redanzCore.model.registration.config.WorkflowStatusConfig;
import ch.redanz.redanzCore.model.registration.service.RegistrationMatchingService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.WorkflowStatusService;
import ch.redanz.redanzCore.model.registration.service.WorkflowTransitionService;
import ch.redanz.redanzCore.service.email.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

  @Autowired
  private Environment environment;

  @Autowired
  Configuration mailConfig;

  //  @Scheduled(cron = "${cron.matching.scheduler.value}")
//  @Scheduled(cron = "0 50 15 * * MON-SUN")
  @Scheduled(cron = "0 0/15 * * * *")
  public void runRelease() throws InterruptedException {
    registrationService.getAllSubmittedRegistrations().forEach(registration -> {
      if (isRelease(registration)) {
        releaseToConfirming(registration);
        try {
          sendEmailConfirmation(registration);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (TemplateException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private boolean isRelease(Registration registration) {
    return
      isMatchingOK(registration)
//      @Todo isCapacityOK?
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

  private void releaseToConfirming(Registration registration) {
    workflowTransitionService.saveWorkflowTransition(
      new WorkflowTransition(
        workflowStatusService.findByWorkflowStatusName(WorkflowStatusConfig.CONFIRMING.getName()),
        registration,
        LocalDateTime.now()
      )
    );
  }
  private void sendEmailConfirmation(Registration registration) throws IOException, TemplateException {
    Map<String, Object> model = new HashMap<>();
    model.put("link", environment.getProperty("link.login"));
    model.put("firstName", registration.getParticipant().getFirstName());
    Template template = mailConfig.getTemplate("registrationReleased.ftl");
//    EmailService emailService = new EmailService();
    EmailService.sendEmail(
      EmailService.getSession(),
      registration.getParticipant().getUser().getEmail(),
      "You have a spot at Redanz",
      FreeMarkerTemplateUtils.processTemplateIntoString(template, model)
    );
  }
}
