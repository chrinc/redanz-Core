package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.entities.Registration;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@AllArgsConstructor
@Slf4j
public class EventRegistrationService {
  private final RegistrationService registrationService;
  private final EventRepo eventRepo;
  private final TrackService trackService;
  public boolean hasRegistration(Event event) {
    return registrationService.countByEvent(event) > 0;
  }

  public boolean slotIsUsedAndHasRegistration(Slot slot) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if (hasRegistration(event)) {
        event.getEventTypeSlots().forEach(eventTypeSlot -> {
          if (eventTypeSlot.getTypeSlot().getSlot().equals(slot)) {
            isUsed.set(true);
          }
        });
      }
    });
    return isUsed.get();
  }

  public boolean hasActiveRegistration(Event event) {
    return registrationService.countActive(event) > 0;
  }

  public boolean foodIsUsedAndHasRegistration(Food food) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if (hasRegistration(event)) {
        event.getEventTypeSlots().forEach(eventTypeSlot -> {
          if (eventTypeSlot.getTypeSlot().getType().equals("food")
            && eventTypeSlot.getTypeSlot().getTypeObjectId().equals(food.getFoodId())
          ) {
            isUsed.set(true);
          }
        });
      }
    });
    return isUsed.get();
  }

  public boolean specialIsUsedAndHasRegistration(Special special) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if (hasRegistration(event)) {
        event.getEventSpecials().forEach(eventSpecial -> {
          if (eventSpecial.getSpecial().equals(special)) {
            isUsed.set(true);
          }
        });
      }
    });
    return isUsed.get();
  }

  public boolean privateIsUsedAndHasRegistration(PrivateClass privateClass) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if (hasRegistration(event)) {
        event.getEventPrivates().forEach(eventPrivateClass -> {
          if (eventPrivateClass.getPrivateClass().equals(privateClass)) {
            isUsed.set(true);
          }

        });
      }
    });
    return isUsed.get();
  }

  public boolean volunteerTypeIsUsedAndHasRegistration(VolunteerType volunteerType) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if( hasRegistration(event)
        && event.getVolunteerTypes().contains(volunteerType)
      ) {
        isUsed.set(true);
      };
    });
    return isUsed.get();
  }

  public boolean discountIsUsedAndHasRegistration(Discount discount) {
    AtomicBoolean isUsed = new AtomicBoolean(false);
    eventRepo.findAll().forEach(event -> {
      if( hasRegistration(event)
        && (
          event.getEventDiscounts().contains(discount)
        )
      ) {
        isUsed.set(true);
      };
    });
    return isUsed.get();
  }

}
