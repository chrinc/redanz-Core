package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
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

  public List<ResponseStats> getStatsReport(Language language, Event event) {
    List<ResponseStats> stats = new ArrayList<>();
    bundleService.getAllByEvent(event).forEach(bundle -> {
      stats.add(
        new ResponseStats(
          "Pass"
          ,bundle.getName()
          ,registrationService.countBundlesSubmittedConfirmingAndDone(bundle)
          ,registrationService.countBundlesConfirmingAndDone(bundle)
          ,registrationService.countBundlesDone(bundle)
         ,bundle.getCapacity()
        )
      );
    });
    log.info("bundles: " );

    trackService.getAllByEvent(event).forEach(track -> {
      stats.add(
        new ResponseStats(
          "Track"
          ,track.getName()
         ,registrationService.countTracksSubmittedConfirmingAndDone(track)
         ,registrationService.countTracksConfirmingAndDone(track)
         ,registrationService.countTracksDone(track)
         ,track.getCapacity()
        )
      );
    });

    stats.add(
      new ResponseStats(
        "Workshop"
        ,event.getName()
        ,registrationService.countSubmittedConfirmingAndDoneByEvent(event)
        ,registrationService.countConfirmingAndDoneByEvent(event)
        ,registrationService.countDoneByEvent(event)
       ,event.getCapacity()
      )
    );

    slotService.getFoodSlotsPairsByEvent(event).forEach(foodSlot -> {
      Food food = (Food) foodSlot.get(0);
      Slot slot = (Slot) foodSlot.get(1);
      stats.add(

        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(food.getName(), language.getLanguageKey()).getOutText()
            + " ("
            + outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText()
            + ")"
          , foodRegistrationService.countFoodSlotSubmittedReleasedAndDoneByEvent(food, slot, event)
          , foodRegistrationService.countFoodSlotConfirmingAndDoneByEvent(food, slot, event)
          , foodRegistrationService.countFoodSlotDoneByEvent(food, slot, event)
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
