package ch.redanz.redanzCore.model.registration.service;

import ch.redanz.redanzCore.model.registration.entities.CheckIn;
import ch.redanz.redanzCore.model.registration.entities.Guest;
import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.repository.CheckInRepo;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.TypeSlot;
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
  private final RegistrationService registrationService;
  public void save(CheckIn checkIn) {
    checkInRepo.save(checkIn);
  }

  public List<CheckIn> findAllByEventAndSlot(Event event, Slot slot) {
    return checkInRepo.findAllByEventAndSlot(event, slot);
  }

  public List<CheckIn> findAllByEvent(Event event) {
    return checkInRepo.findAllByEvent(event);
  }
  public List<CheckIn> findRemainingByEventSlot(Event event, Slot slot) {
    return checkInRepo.findAllByEventAndSlotAndCheckInTimeIsNull(event, slot);
  }
  public List<CheckIn> findRemainingByEvent(Event event) {
    return checkInRepo.findAllByEventAndCheckInTimeIsNull(event);
  }
  public List<CheckIn> findCheckedInByEventSlot(Event event, Slot slot) {
    return checkInRepo.findAllByEventAndSlotAndCheckInTimeIsNotNull(event, slot);
  }
  public void deleteAllByEventAndSlot(Event event, Slot slot) {
    checkInRepo.deleteAllByEventAndSlot(event, slot);
  }
  public void deleteAllByEvent(Event event) {
    checkInRepo.deleteAllByEvent(event);
  }
  public void checkIn(CheckIn checkIn) {
    checkIn.setCheckInTime(ZonedDateTime.now());
    save(checkIn);
  }

  public void update(Guest guest, Event event, Slot slot) {
    deleteAllByEventAndSlot(event, slot);

    if (checkInRepo.findByEventAndSlotAndGuest(event, slot, guest).isPresent()) {
      // nothing to do in the moment?
    } else {
      save(
        new CheckIn(
          guest, event, slot
        )
      );
    }
  }

  public void update(Registration registration, Event event, Slot slot) {
    deleteAllByEventAndSlot(event, slot);

    if (checkInRepo.findByEventAndSlotAndRegistration(event, slot, registration).isPresent()) {
      // nothing to do in the moment?
    } else {
      save(
        new CheckIn(
          registration, event, slot
        )
      );
    }
  }


  public void resetByEvent(Event event) {
    deleteAllByEvent(event);

    List<CheckIn> checkIns= new ArrayList<>();
    guestService.findAllByEvent(event).forEach(guest -> {
      if (guest.getSlots().isEmpty()) {
        checkIns.add(
          new CheckIn(
            guest,
            event
          )
        );
      } else {
        guest.getSlots().forEach(slot -> {
          checkIns.add(
            new CheckIn(
              guest,
              event,
              slot
            )
          );
        });
      }
    });

    registrationService.findAllByEvent(event).forEach(registration -> {
      if (registration.getBundle().getPartySlots().isEmpty()) {
        checkIns.add(
          new CheckIn(
            registration,
            event
          )
        );
      } else {
        registration.getBundle().getPartySlots().forEach(slot -> {
          checkIns.add(
            new CheckIn(
              registration,
              event,
              slot
            )
          );
        });
      }
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
