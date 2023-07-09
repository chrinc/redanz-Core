package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
import ch.redanz.redanzCore.model.workshop.entities.Bundle;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Food;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReportStatsService {
  private final RegistrationService registrationService;
  private final BundleService bundleService;
  private final OutTextService outTextService;
  private final TrackService trackService;
  private final SlotService slotService;
  private final FoodRegistrationService foodRegistrationService;
  private final DiscountService discountService;
  private final DiscountRegistrationService discountRegistrationService;
  private final SpecialService specialService;
  private final SpecialRegistrationService specialRegistrationService;

  public List<ResponseStats> getStatsReport(Language language, Event event) {
    List<ResponseStats> stats = new ArrayList<>();
    // log.info("event EventId:" + event.getEventId());

    bundleService.getAllByEvent(event).forEach(bundle -> {
      stats.add(
        new ResponseStats(
          "Pass"
          ,bundle.getName()
          ,registrationService.countBundlesSubmittedConfirmingAndDone(bundle, event)
          ,registrationService.countBundlesConfirmingAndDone(bundle, event)
          ,registrationService.countBundlesDone(bundle, event)
         ,bundle.getCapacity()
        )
      );
    });
    // log.info("bundles: " );
    trackService.getAllByEvent(event).forEach(track -> {
      stats.add(
        new ResponseStats(
          "Track"
          ,track.getName()
         ,registrationService.countTracksSubmittedConfirmingAndDone(track, event)
         ,registrationService.countTracksConfirmingAndDone(track, event)
         ,registrationService.countTracksDone(track, event)
         ,track.getCapacity()
        )
      );
    });

    specialService.findByEvent(event).forEach(special -> {
      stats.add(
        new ResponseStats(
          "Special"
          ,outTextService.getOutTextByKeyAndLangKey(special.getName(), language.getLanguageKey()).getOutText()
         ,specialRegistrationService.countSpecialRegistrations(special, event)
         ,specialRegistrationService.countSpecialsConfirmingAndDone(special, event)
         ,specialRegistrationService.countSpecialsDone(special, event)
         ,special.getCapacity()
        )
      );
    });

    stats.add(
      new ResponseStats(
        "Workshop"
        ,event.getName()
        ,registrationService.countSubmittedConfirmingAndDone(event)
        ,registrationService.countConfirmingAndDone(event)
        ,registrationService.countDone(event)
       ,event.getCapacity()
      )
    );
    slotService.getFoodSlotsPairsByEvent(event).forEach(foodSlot -> {
      Food food = (Food) foodSlot.get(0);
      Slot slot = (Slot) foodSlot.get(1);

      // log.info("foodSlot, food" + ((Food) foodSlot.get(0)).getName());
      // log.info("foodSlot, slot" + ((Slot) foodSlot.get(1)).getName());
      stats.add(
        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText()
          , foodRegistrationService.countFoodSlotSubmittedReleasedAndDone(food, slot, event)
          , foodRegistrationService.countFoodSlotConfirmingAndDone(food, slot, event)
          , foodRegistrationService.countFoodSlotDone(food, slot, event)
          , null
        )
      );
    });

    discountService.findAllByEvent(event).forEach(discount -> {
      stats.add(
        new ResponseStats(
          "Discount"
          ,outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countDiscountSubmittedConfirmingAndDoneByEvent(discount, event)
          , discountRegistrationService.countDiscountConfirmingAndDoneByEvent(discount, event)
          , discountRegistrationService.countDiscountDoneByEvent(discount, event)
          , discount.getCapacity()
        )
      );
    });
    return stats;
  }
}
