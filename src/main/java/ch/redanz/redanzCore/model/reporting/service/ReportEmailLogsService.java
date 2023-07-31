package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.entities.RegistrationMatching;
import ch.redanz.redanzCore.model.registration.entities.WorkflowStatus;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseEmailLogs;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistration;
import ch.redanz.redanzCore.model.reporting.response.ResponseRegistrationDetails;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportEmailLogsService {
  private final RegistrationService registrationService;
  private final PaymentService paymentService;
  private final RegistrationEmailService registrationEmailService;


  public List<ResponseEmailLogs> getEmailLogs(Event event) {
    List<ResponseEmailLogs> responseEmailLogs = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // hack hack hack @todo: change timestamps to timezone.
    Integer timeDifference = 2;

    registrationService.findAllByEvent(event).forEach(registration -> {
        RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
        responseEmailLogs.add(
          new ResponseEmailLogs(
            registration.getParticipant().getUser().getUserId(),
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            registration.getParticipant().getUser().getEmail(),
            registration.getBundle().getName(),
            registration.getWorkflowStatus().getName(),
            paymentService.amountDue(registration),
            paymentService.totalAmount(registration),
            registrationEmail == null || registrationEmail.getReceivedSentDate() == null ?  "" : registrationEmail.getReceivedSentDate().plusHours(timeDifference).format(formatter),
            registrationEmail == null || registrationEmail.getReleasedSentDate() == null ?  "" : registrationEmail.getReleasedSentDate().plusHours(timeDifference).format(formatter),
            registrationEmail == null || registrationEmail.getDoneSentDate() == null ?  "" : registrationEmail.getDoneSentDate().plusHours(timeDifference).format(formatter),
            registrationEmail == null || registrationEmail.getReminderSentDate() == null ?  "" : registrationEmail.getReminderSentDate().plusHours(timeDifference).format(formatter),
            registrationEmail == null || registrationEmail.getCancelledSentDate() == null ?  "" : registrationEmail.getCancelledSentDate().plusHours(timeDifference).format(formatter)
          )
        );
    });
    return responseEmailLogs;
  }
}
