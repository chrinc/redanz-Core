package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.repository.CheckInRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.PrivateClassService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CheckInService {
  private final SlotService slotService;
  private final CheckInRepo checkInRepo;
  private final GuestService guestService;
  private final FoodRegistrationService foodRegistrationService;
  private final RegistrationService registrationService;
  private final SpecialRegistrationService specialRegistrationService;
  private final DiscountRegistrationService discountRegistrationService;
  private final PrivateClassService privateClassService;
  private final LanguageService languageService;
  private final PaymentService paymentService;
  public void save(CheckIn checkIn) {
    checkInRepo.save(checkIn);
  }

  public List<CheckIn> findAllByEvent(Event event) {
    return checkInRepo.findAllByEvent(event);
  }
  public void deleteAllByEvent(Event event) {
    checkInRepo.deleteAllByEvent(event);
  }
  public void checkIn(CheckIn checkIn) {
    checkIn.setCheckInTime(ZonedDateTime.now());
    save(checkIn);
  }

  public void resetByEvent(Event event) {
    deleteAllByEvent(event);

    List<CheckIn> checkIns = new ArrayList<>();
    guestService.findAllByEvent(event).forEach(guest -> {
      checkIns.add(
      new CheckIn(
        guest.getEvent(),
        guest,
        guest.getName(),
        "Guest",
        guest.getDescription(),
        slotService.slotNames(guest.getSlots(), languageService.english())
      )
      );
    });

    registrationService.findAllByEvent(event).forEach(registration -> {
      checkIns.add(
      new CheckIn(
        registration.getEvent(),
        registration,
        registration.getParticipant().getFirstName() + " " +  registration.getParticipant().getLastName(),
        registration.getBundle().getName(),
        registration.getTrack() != null ? registration.getTrack().getName() : "",
        slotService.slotNames(registration.getBundle().getPartySlots(), languageService.english()),
        foodRegistrationService.getReportFoodSlots(registration, languageService.english()),
        specialRegistrationService.getReportSpecials(registration, languageService.english()),
        discountRegistrationService.getReportDiscounts(registration, languageService.english()),
        privateClassService.getReportPrivates(registration, languageService.english()),
        registrationService.workflowStatusName(registration),
        paymentService.amountDue(registration),
        paymentService.totalAmount(registration),
        registration.getBundle().getColor()
       )
      );
    });
    checkInRepo.saveAll(checkIns);
  }
  public CheckIn findById(Long checkInId) {
    return checkInRepo.findByCheckInId(checkInId);
  }

  public void checkInRequest(JsonObject request) {
    checkIn(findById(request.get("checkInId").getAsLong()));
  }
}
