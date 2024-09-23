package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.*;
import ch.redanz.redanzCore.model.reporting.response.ResponseCheckIn;
import ch.redanz.redanzCore.model.workshop.configTest.LanguageConfig;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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
  private final RegistrationService registrationService;

  @Transactional
  public List<ResponseCheckIn> getCheckinReport(Event event) {
    List<ResponseCheckIn> responseCheckIns = Collections.synchronizedList(new ArrayList<>());

    // Fetch all check-ins for the event
    List<CheckIn> checkIns = checkInService.findAllByEvent(event);

    // Pre-fetch and initialize lazy-loaded entities before parallel processing
    checkIns.forEach(checkIn -> {
      Hibernate.initialize(checkIn.getRegistration());
      Registration registration = checkIn.getRegistration();
      if (registration != null) {
        Hibernate.initialize(registration.getParticipant());
        Hibernate.initialize(registration.getBundle());
        Hibernate.initialize(registration.getTrack());
        if (registration.getBundle() != null) {
          Hibernate.initialize(registration.getBundle().getColor());
        }
      }
    });

    // Parallel processing of check-ins
    checkIns.parallelStream().forEach(checkIn -> {
      boolean isGuest = checkIn.getGuest() != null;
      String bundleColor = (checkIn.getRegistration() != null && checkIn.getRegistration().getBundle() != null && checkIn.getRegistration().getBundle().getColor() != null) ? checkIn.getRegistration().getBundle().getColor() : null;
      String slotColor = (checkIn.getSlot() != null && checkIn.getSlot().getColor() != null) ? checkIn.getSlot().getColor() : null;


      try {
        // Create ResponseCheckIn object
        ResponseCheckIn responseCheckIn = new ResponseCheckIn(
          checkIn.getCheckInId(),
          isGuest ? checkIn.getGuest().getName() : registrationService.fullName(checkIn.getRegistration()),
          isGuest ? "Guest" : registrationService.bundleName(checkIn.getRegistration()),
          isGuest ? checkIn.getGuest().getDescription() : registrationService.trackName(checkIn.getRegistration()),
          null, // Check-in slot, if needed, add handling
          isGuest ? "" : foodRegistrationService.getReportFoodSlots(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
          isGuest ? "" : specialRegistrationService.getReportSpecials(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
          isGuest ? "" : discountRegistrationService.getReportDiscounts(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
          isGuest ? "" : privateClassService.getReportPrivates(checkIn.getRegistration(), languageService.findLanguageByLanguageKey(LanguageConfig.ENGLISH.getKey())),
          isGuest ? "" : registrationService.workflowStatusName(checkIn.getRegistration()),
//          isGuest ? null : null, // paymentService.amountDue(checkIn.getRegistration()),
//          isGuest ? null : null, // paymentService.totalAmount(checkIn.getRegistration()),
          isGuest ? slotColor : bundleColor, // Color info, slot or bundle color
          checkIn.getCheckInTime()
        );

        // Add the response to the thread-safe list
        responseCheckIns.add(responseCheckIn);
      } catch (ApiRequestException apiRequestException) {
        log.error("API request error while processing check-in: " + apiRequestException.getMessage());
        throw new ApiRequestException(OutTextConfig.LABEL_ERROR_SUBMIT_GE.getOutTextKey());
      } catch (Exception exception) {
        log.error("Unexpected error while processing check-in: " + exception.getMessage());
        throw new ApiRequestException(exception.getMessage());
      }
    });
    return responseCheckIns;
  }
}
