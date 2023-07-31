package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseCheckIn;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportCheckinService {
  private final CheckInService checkInService;
  private final PaymentService paymentService;

  public List<ResponseCheckIn> getCheckinReport(Event event) {
    List<ResponseCheckIn> responseCheckIns = new ArrayList<>();

    checkInService.findAllByEvent(event).parallelStream().forEach(checkIn -> {
      boolean isGuest = checkIn.getGuest() != null;
//      if (isGuest) {
        responseCheckIns.add(
          new ResponseCheckIn(
            checkIn.getCheckInId(),
            isGuest ? checkIn.getGuest().getName() : checkIn.getRegistration().getParticipant().getFirstName() + " " + checkIn.getRegistration().getParticipant().getLastName(),
            isGuest ? "Guest" : checkIn.getRegistration().getBundle().getName(),
            isGuest ? checkIn.getGuest().getDescription() : checkIn.getRegistration().getTrack() == null ? "" : checkIn.getRegistration().getTrack().getName(),
            checkIn.getSlot() == null ? null : checkIn.getSlot(),
            isGuest ? "" : checkIn.getRegistration().getWorkflowStatus().getName(),
            isGuest ? null : paymentService.amountDue(checkIn.getRegistration()),
            isGuest ? null : paymentService.totalAmount(checkIn.getRegistration()),
            checkIn.getCheckInTime()
          )
        );
//      }
    });

    return responseCheckIns;
  }
}
