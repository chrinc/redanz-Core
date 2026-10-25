package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    // amounts loaded once for all registrations of the event (not per registration: slow with many registrations)
    List<Registration> registrations = registrationService.findAllByEvent(event);
    Map<Long, PaymentService.Amounts> amounts = paymentService.amountsByRegistration(event, registrations);
    Map<Long, RegistrationEmail> registrationEmails = registrationEmailService.findAllByEvent(event);
    Map<String, String> workflowStatusTexts = new HashMap<>();
    registrations.forEach(registration -> {
        RegistrationEmail registrationEmail = registrationEmails.get(registration.getRegistrationId());
        responseEmailLogs.add(
          new ResponseEmailLogs(
            registration.getParticipant().getPersonId(),
            registration.getRegistrationId(),
            registration.getParticipant().getFirstName(),
            registration.getParticipant().getLastName(),
            registration.getParticipant().getEmail(),
            registration.getBundle().getName(),
            workflowStatusTexts.computeIfAbsent(registration.getWorkflowStatus().getLabel(), label -> outTextService.getOutTextMapByKey(label).toString()),
            amounts.get(registration.getRegistrationId()).amountDue(),
            amounts.get(registration.getRegistrationId()).totalAmount(),
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
