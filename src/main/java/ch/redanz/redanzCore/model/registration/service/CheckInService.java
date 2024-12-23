package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.repository.CheckInRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.*;
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
  private final VolunteerService volunteerService;
  private final BundleService bundleService;
  private final EventService eventService;
  private final BundleEventTrackService bundleEventTrackService;
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
  public void resetCheckIn(CheckIn checkIn) {
    checkIn.setCheckInTime(null);
    save(checkIn);
  }

  public void resetByEvent(Event event) {
    deleteAllByEvent(event);

    List<CheckIn> checkIns = new ArrayList<>();

    // Guests
    guestService.findAllByEvent(event).forEach(guest -> {
      checkIns.add(
      new CheckIn(
        guest.getEvent(),
        guest,
        guest.getName(),
        "Guest",
        guest.getDescription(),
        slotService.slotNames(guest.getSlots(), languageService.english()),
        "d3d3d3" // grey
      )
      );
    });

    // Staff
    registrationService.findStaffByEvent(event).forEach(staff -> {
      checkIns.add(
      new CheckIn(
        staff.getEvent(),
        staff,
        staff.getParticipant().getFirstName() + " " +  staff.getParticipant().getLastName(),
        RegistrationType.STAFF.name(),
        volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), languageService.english()) : "",
        "",
        "e4e4e4"
       )
      );
    });

    // Participants
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
        bundleService.hasTrack(registration.getBundle()) ? bundleEventTrackService.findByEventBundleAndTrack(registration.getEvent(), registration.getBundle(),registration.getTrack()).getColor() : registration.getBundle().getColor(),
        registration.getDanceRole() != null ? registration.getDanceRole().getName() : ""
       )
      );
    });
    checkInRepo.saveAll(checkIns);
  }

  public void updateByEvent(Event event) {
//    deleteAllByEvent(event);

    List<CheckIn> checkIns = new ArrayList<>();
    List<CheckIn> currentCheckIns = checkInRepo.findAllByEvent(event);

    // Guests
    guestService.findAllByEvent(event).forEach(guest -> {
      if (checkInRepo.existsByGuest(guest)) {
        CheckIn existingCheckIn = checkInRepo.findByGuest(guest);
        existingCheckIn.setEvent(guest.getEvent());
        existingCheckIn.setCheckinName(guest.getName());
        existingCheckIn.setDescription(guest.getDescription());
        existingCheckIn.setSlots(slotService.slotNames(guest.getSlots(), languageService.english()));
        checkIns.add(existingCheckIn);
      } else {
         checkIns.add(
          new CheckIn(
            guest.getEvent(),
            guest,
            guest.getName(),
            "Guest",
            guest.getDescription(),
            slotService.slotNames(guest.getSlots(), languageService.english()),
            "d3d3d3" // grey
          )
        );
      }
    });

    // Staff
    registrationService.findStaffByEvent(event).forEach(staff -> {
      if (checkInRepo.existsByRegistration(staff)) {
        CheckIn existingCheckIn = checkInRepo.findByRegistration(staff);
        existingCheckIn.setEvent(staff.getEvent());
        existingCheckIn.setCheckinName(staff.getParticipant().getFirstName() + " " + staff.getParticipant().getLastName());
        existingCheckIn.setType(RegistrationType.STAFF.name());
        existingCheckIn.setDescription(volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), languageService.english()) : "");
        checkIns.add(existingCheckIn);
      } else {
        checkIns.add(
          new CheckIn(
            staff.getEvent(),
            staff,
            staff.getParticipant().getFirstName() + " " + staff.getParticipant().getLastName(),
            RegistrationType.STAFF.name(),
            volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), languageService.english()) : "",
            "",
            "e4e4e4"
          )
        );
      }
    });

    // Participants
    registrationService.findAllByEvent(event).forEach(registration -> {
      if (checkInRepo.existsByRegistration(registration)) {
        CheckIn existingCheckIn = checkInRepo.findByRegistration(registration);
        existingCheckIn.setEvent(registration.getEvent());
        existingCheckIn.setCheckinName(registration.getParticipant().getFirstName() + " " + registration.getParticipant().getLastName());
        existingCheckIn.setType(registration.getBundle().getName());
        existingCheckIn.setDescription(registration.getTrack() != null ? registration.getTrack().getName() : "");
        existingCheckIn.setSlots(slotService.slotNames(registration.getBundle().getPartySlots(), languageService.english()));
        existingCheckIn.setFood(foodRegistrationService.getReportFoodSlots(registration, languageService.english()));
        existingCheckIn.setAddons(specialRegistrationService.getReportSpecials(registration, languageService.english()));
        existingCheckIn.setDiscounts(discountRegistrationService.getReportDiscounts(registration, languageService.english()));
        existingCheckIn.setPrivates(privateClassService.getReportPrivates(registration, languageService.english()));
        existingCheckIn.setStatus(registrationService.workflowStatusName(registration));
        existingCheckIn.setAmountDue(paymentService.amountDue(registration));
        existingCheckIn.setTotalAmount(paymentService.totalAmount(registration));
        existingCheckIn.setColor(bundleService.hasTrack(registration.getBundle()) ? bundleEventTrackService.findByEventBundleAndTrack(registration.getEvent(), registration.getBundle(), registration.getTrack()).getColor() : registration.getBundle().getColor());
        existingCheckIn.setDanceRole(registration.getDanceRole() != null ? registration.getDanceRole().getName() : "");
        checkIns.add(existingCheckIn);
      } else {
        checkIns.add(
          new CheckIn(
            registration.getEvent(),
            registration,
            registration.getParticipant().getFirstName() + " " + registration.getParticipant().getLastName(),
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
            bundleService.hasTrack(registration.getBundle()) ? bundleEventTrackService.findByEventBundleAndTrack(registration.getEvent(), registration.getBundle(), registration.getTrack()).getColor() : registration.getBundle().getColor(),
            registration.getDanceRole() != null ? registration.getDanceRole().getName() : ""
          )
        );
      }
    });

    if (currentCheckIns.size() != 0) {
      for (CheckIn currentCheckIn : currentCheckIns) {
        if (!checkIns.contains(currentCheckIn)) {
          checkInRepo.deleteByCheckInId(currentCheckIn.getCheckInId());
        }
      }
    }

    checkInRepo.saveAll(checkIns);
  }
  public CheckIn findById(Long checkInId) {
    return checkInRepo.findByCheckInId(checkInId);
  }

  public void checkInRequest(JsonObject request) {
    checkIn(findById(request.get("checkInId").getAsLong()));
  }

  public void resetCheckInRequest(JsonObject request) {
    resetCheckIn(findById(request.get("checkInId").getAsLong()));
  }
}
