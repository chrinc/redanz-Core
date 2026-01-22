package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.entities.RegistrationEmail;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseEmailLogs;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.OutTextService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportEmailLogsService {
  private final RegistrationService registrationService;
  private final PaymentService paymentService;
  private final RegistrationEmailService registrationEmailService;
  private final OutTextService outTextService;


  public List<ResponseEmailLogs> getEmailLogs(Event event) {
    List<ResponseEmailLogs> responseEmailLogs = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // hack hack hack @todo: change timestamps to timezone.
    Integer timeDifference = 2;

    registrationService.findAllByEvent(event).forEach(registration -> {
        RegistrationEmail registrationEmail = registrationEmailService.findByRegistration(registration);
        responseEmailLogs.add(
          new ResponseEmailLogs(
            registration.getParticipant().getPersonId(),
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            registration.getParticipant().getEmail(),
            registration.getBundle().getName(),
            outTextService.getOutTextMapByKey(registration.getWorkflowStatus().getLabel()).toString(),
            paymentService.amountDue(registration),
            paymentService.totalAmount(registration),
            registrationEmail == null || registrationEmail.getReceivedSentDate() == null ?  "" : registrationEmail.getReceivedSentDate().toString(),
            registrationEmail == null || registrationEmail.getReleasedSentDate() == null ?  "" : registrationEmail.getReleasedSentDate().toString(),
            registrationEmail == null || registrationEmail.getDoneSentDate() == null ?  "" : registrationEmail.getDoneSentDate().toString(),
            registrationEmail == null || registrationEmail.getReminderSentDate() == null ?  "" : registrationEmail.getReminderSentDate().toString(),
            registrationEmail == null || registrationEmail.getCancelledSentDate() == null ?  "" : registrationEmail.getCancelledSentDate().toString()
          )
        );
    });
    return responseEmailLogs;
  }
}
