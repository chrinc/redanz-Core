package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.profile.service.LanguageService;
import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.entities.RegistrationType;
import ch.redanz.redanzCore.model.registration.repository.CheckInRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.*;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
  private final HostingService hostingService;

  public void save(CheckIn checkIn) {
    checkInRepo.save(checkIn);
  }

  public List<CheckIn> findAllByEvent(Event event) {
    return checkInRepo.findAllByEvent(event);
  }
  public void deleteAllByEvent(Event event) {
    checkInRepo.bulkDeleteByEvent(event);
  }
  public void checkIn(CheckIn checkIn) {
    checkIn.setCheckInTime(ZonedDateTime.now());
    save(checkIn);
  }
  public void resetCheckIn(CheckIn checkIn) {
    checkIn.setCheckInTime(null);
    save(checkIn);
  }

  // everything the check-ins of an event need, loaded once for the whole event
  // (instead of several queries per registration): reports by registration id, party slots by bundle id
  private record EventCheckInData(
    Language english,
    List<Registration> registrations,
    Set<Long> hostRegistrationIds,
    Map<Long, PaymentService.Amounts> amounts,
    Map<Long, String> foods,
    Map<Long, String> specials,
    Map<Long, String> discounts,
    Map<Long, String> privates,
    Map<Long, String> partySlotsByBundleId
  ) {
    boolean isHost(Registration registration) {
      return hostRegistrationIds.contains(registration.getRegistrationId());
    }
  }

  private EventCheckInData eventCheckInData(Event event) {
    Language english = languageService.english();
    List<Registration> registrations = registrationService.findAllByEvent(event);
    return new EventCheckInData(
      english,
      registrations,
      hostingService.getAllHostRegistrationsByEvent(event).stream()
        .map(hostRegistration -> hostRegistration.getRegistration().getRegistrationId())
        .collect(Collectors.toSet()),
      paymentService.amountsByRegistration(event, registrations),
      foodRegistrationService.getReportFoodSlotsByRegistration(event),
      specialRegistrationService.getReportSpecialsByRegistration(event),
      discountRegistrationService.getReportDiscountsByRegistration(event, english),
      privateClassService.getReportPrivatesByRegistration(event),
      new HashMap<>()
    );
  }

  private String partySlots(Registration registration, EventCheckInData data) {
    return data.partySlotsByBundleId().computeIfAbsent(
      registration.getBundle().getBundleId(),
      bundleId -> slotService.slotNames(registration.getBundle().getPartySlots(), data.english())
    );
  }

  private static String workflowStatusName(Registration registration) {
    return registration.getWorkflowStatus() != null ? registration.getWorkflowStatus().getName() : null;
  }

  public void resetByEvent(Event event) {
    deleteAllByEvent(event);

    List<CheckIn> checkIns = new ArrayList<>();
    EventCheckInData data = eventCheckInData(event);

    // Guests
    guestService.findAllByEvent(event).forEach(guest -> {
      checkIns.add(
      new CheckIn(
        guest.getEvent(),
        guest,
        guest.getName(),
        "Guest",
        guest.getDescription(),
        slotService.slotNames(guest.getSlots(), data.english()),
        "d3d3d3" // grey
      )
      );
    });

    // Staff
    registrationService.findStaffByEvent(event).forEach(staff -> {
      if (!data.isHost(staff)) {
        checkIns.add(
          new CheckIn(
            staff.getEvent(),
            staff,
            staff.getParticipant().getFirstName() + " " + staff.getParticipant().getLastName(),
            RegistrationType.STAFF.name(),
            volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), data.english()) : "",
            "",
            "e4e4e4"
          )
        );
      }
    });

    // Participants
    data.registrations().forEach(registration -> {
      Long registrationId = registration.getRegistrationId();
      PaymentService.Amounts registrationAmounts = data.amounts().get(registrationId);
      checkIns.add(
      new CheckIn(
        registration.getEvent(),
        registration,
        registration.getParticipant().getFirstName()
          + " " +  registration.getParticipant().getLastName()
          + (data.isHost(registration) ? " (Host)" : ""),
        registration.getBundle().getName(),
        registration.getTrack() != null ? registration.getTrack().getName() : "",
        partySlots(registration, data),
        data.foods().getOrDefault(registrationId, ""),
        data.specials().getOrDefault(registrationId, ""),
        data.discounts().getOrDefault(registrationId, ""),
        data.privates().getOrDefault(registrationId, ""),
        workflowStatusName(registration),
        registrationAmounts.amountDue(),
        registrationAmounts.totalAmount(),
        registration.getBundle().getColor() /* wrist band color of the bundle */,
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

    // existing check-ins by guest / registration (instead of two queries per person)
    Map<Long, CheckIn> checkInByGuestId = new HashMap<>();
    Map<Long, CheckIn> checkInByRegistrationId = new HashMap<>();
    currentCheckIns.forEach(checkIn -> {
      if (checkIn.getGuest() != null) checkInByGuestId.putIfAbsent(checkIn.getGuest().getGuestId(), checkIn);
      if (checkIn.getRegistration() != null) checkInByRegistrationId.putIfAbsent(checkIn.getRegistration().getRegistrationId(), checkIn);
    });

    EventCheckInData data = eventCheckInData(event);

    // Guests
    guestService.findAllByEvent(event).forEach(guest -> {
      CheckIn existingCheckIn = checkInByGuestId.get(guest.getGuestId());
      if (existingCheckIn != null) {
        existingCheckIn.setEvent(guest.getEvent());
        existingCheckIn.setCheckinName(guest.getName());
        existingCheckIn.setDescription(guest.getDescription());
        existingCheckIn.setSlots(slotService.slotNames(guest.getSlots(), data.english()));
        checkIns.add(existingCheckIn);
      } else {
         checkIns.add(
          new CheckIn(
            guest.getEvent(),
            guest,
            guest.getName(),
            "Guest",
            guest.getDescription(),
            slotService.slotNames(guest.getSlots(), data.english()),
            "d3d3d3" // grey
          )
        );
      }
    });

    // Staff
    registrationService.findStaffByEvent(event).forEach(staff -> {
      CheckIn existingCheckIn = checkInByRegistrationId.get(staff.getRegistrationId());
      if (existingCheckIn != null) {
        existingCheckIn.setEvent(staff.getEvent());
        existingCheckIn.setCheckinName(staff.getParticipant().getFirstName() + " " + staff.getParticipant().getLastName());
        existingCheckIn.setType(RegistrationType.STAFF.name());
        existingCheckIn.setDescription(volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), data.english()) : "");
        checkIns.add(existingCheckIn);
      } else {
        if (!data.isHost(staff)) {
          checkIns.add(
            new CheckIn(
              staff.getEvent(),
              staff,
              staff.getParticipant().getFirstName() + " " + staff.getParticipant().getLastName(),
              RegistrationType.STAFF.name(),
              volunteerService.hasVolunteerRegistration(staff) ? volunteerService.volunteerTypeName(volunteerService.findByRegistration(staff), data.english()) : "",
              "",
              "e4e4e4"
            )
          );
        }
      }
    });

    // Participants
    data.registrations().forEach(registration -> {
      Long registrationId = registration.getRegistrationId();
      CheckIn existingCheckIn = checkInByRegistrationId.get(registrationId);
      PaymentService.Amounts registrationAmounts = data.amounts().get(registrationId);
      boolean isHost = data.isHost(registration);
      if (existingCheckIn != null) {
        existingCheckIn.setEvent(registration.getEvent());
        existingCheckIn.setCheckinName(
                  registration.getParticipant().getFirstName()
          + " " + registration.getParticipant().getLastName()
          + (isHost ? " (Host)" : "")
        );
        existingCheckIn.setType(registration.getBundle().getName());
        existingCheckIn.setDescription(registration.getTrack() != null ? registration.getTrack().getName() : "");
        existingCheckIn.setSlots(partySlots(registration, data));
        existingCheckIn.setFood(data.foods().getOrDefault(registrationId, ""));
        existingCheckIn.setAddons(data.specials().getOrDefault(registrationId, ""));
        existingCheckIn.setDiscounts(data.discounts().getOrDefault(registrationId, ""));
        existingCheckIn.setPrivates(data.privates().getOrDefault(registrationId, ""));
        existingCheckIn.setStatus(workflowStatusName(registration));
        existingCheckIn.setAmountDue(registrationAmounts.amountDue());
        existingCheckIn.setTotalAmount(registrationAmounts.totalAmount());
        existingCheckIn.setColor(registration.getBundle().getColor() /* wrist band color of the bundle */);
        existingCheckIn.setDanceRole(registration.getDanceRole() != null ? registration.getDanceRole().getName() : "");
        checkIns.add(existingCheckIn);
      } else {
        checkIns.add(
          new CheckIn(
            registration.getEvent(),
            registration,
            registration.getParticipant().getFirstName()
              + " " + registration.getParticipant().getLastName()
              + (isHost ? " (Host)" : ""),
            registration.getBundle().getName(),
            registration.getTrack() != null ? registration.getTrack().getName() : "",
            partySlots(registration, data),
            data.foods().getOrDefault(registrationId, ""),
            data.specials().getOrDefault(registrationId, ""),
            data.discounts().getOrDefault(registrationId, ""),
            data.privates().getOrDefault(registrationId, ""),
            workflowStatusName(registration),
            registrationAmounts.amountDue(),
            registrationAmounts.totalAmount(),
            registration.getBundle().getColor() /* wrist band color of the bundle */,
            registration.getDanceRole() != null ? registration.getDanceRole().getName() : ""
          )
        );
      }
    });

    // remove check-ins that no longer exist (one delete statement)
    Set<CheckIn> keptCheckIns = Collections.newSetFromMap(new IdentityHashMap<>());
    keptCheckIns.addAll(checkIns);
    List<CheckIn> removedCheckIns = currentCheckIns.stream()
      .filter(checkIn -> !keptCheckIns.contains(checkIn))
      .collect(Collectors.toList());
    if (!removedCheckIns.isEmpty()) {
      checkInRepo.deleteAllInBatch(removedCheckIns);
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
