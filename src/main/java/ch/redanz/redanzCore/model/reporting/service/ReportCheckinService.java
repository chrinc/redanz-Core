package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseCheckIn;
import ch.redanz.redanzCore.model.workshop.config.LanguageConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportCheckinService {
  private final CheckInService checkInService;
  private final PaymentService paymentService;
  private final FoodRegistrationService foodRegistrationService;
  private final LanguageService languageService;
  private final SpecialRegistrationService specialRegistrationService;
  private final PrivateClassService privateClassService;
  private final DiscountRegistrationService discountRegistrationService;

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
            isGuest ? "" : foodRegistrationService.getReportFoodSlots(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
            isGuest ? "": specialRegistrationService.getReportSpecials(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
            isGuest ? "": discountRegistrationService.getReportDiscounts(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
            isGuest ? "": privateClassService.getReportPrivates(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
            isGuest ? "" : checkIn.getRegistration().getWorkflowStatus().getName(),
            isGuest ? null : paymentService.amountDue(checkIn.getRegistration()),
            isGuest ? null : paymentService.totalAmount(checkIn.getRegistration()),
            isGuest ? checkIn.getSlot() == null ? null : checkIn.getSlot().getColor() : checkIn.getRegistration().getBundle() .getColor(),
            checkIn.getCheckInTime()
          )
        );
//      }
    });

    return responseCheckIns;
  }
}
