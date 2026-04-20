package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.repository.PrivateClassRegistrationRepo;
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
  private final PrivateClassRegistrationRepo privateClassRegistrationRepo;
  private final DiscountService discountService;

  public boolean hasRegistration(Event event) {
    return registrationService.countByEvent(event) > 0;
  }

//  public boolean slotIsUsedAndHasRegistration(Slot slot) {
//    AtomicBoolean isUsed = new AtomicBoolean(false);
//    eventRepo.findAll().forEach(event -> {
//      if (hasRegistration(event)) {
//        event.getEventSlots().forEach(eventTypeSlot -> {
//          if (eventTypeSlot.getTypeSlot().getSlot().equals(slot)) {
//            isUsed.set(true);
//          }
//        });
//      }
//    });
//    return isUsed.get();
//  }

  public boolean hasActiveRegistration(Event event) {
    return registrationService.countActive(event) > 0;
  }

//  public boolean foodIsUsedAndHasRegistration(EventFoodSlot eventFoodSlot) {
//    AtomicBoolean isUsed = new AtomicBoolean(false);
//    eventRepo.findAll().forEach(event -> {
//      if (hasRegistration(event)) {
//        event.getEventSlots().forEach(eventTypeSlot -> {
//          if (eventTypeSlot.getTypeSlot().getType().equals("food")
//            && eventTypeSlot.getTypeSlot().getTypeObjectId().equals(food.getFoodId())
//          ) {
//            isUsed.set(true);
//          }
//        });
//      }
//    });
//    return isUsed.get();
//  }

//  public boolean specialIsUsedAndHasRegistration(Special special) {
//    AtomicBoolean isUsed = new AtomicBoolean(false);
//    eventRepo.findAll().forEach(event -> {
//      if (hasRegistration(event)) {
//        event.getEventSpecials().forEach(eventSpecial -> {
//          if (eventSpecial.getSpecial().equals(special)) {
//            isUsed.set(true);
//          }
//        });
//      }
//    });
//    return isUsed.get();
//  }

  public boolean privateHasRegistration(EventPrivateClass eventPrivateClass) {
    return privateClassRegistrationRepo.existsByEventPrivateClassAndRegistrationActive(eventPrivateClass, true);
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

  public boolean eventDiscountHasRegistration(EventDiscount eventDiscount) {
    return discountService.hasRegistration(eventDiscount, true);
  }

}
