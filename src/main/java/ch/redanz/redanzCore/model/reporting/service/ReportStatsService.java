package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
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
  private final EventService eventService;
  private final SlotService slotService;
  private final FoodRegistrationService foodRegistrationService;
  private final DiscountService discountService;
  private final DiscountRegistrationService discountRegistrationService;

  public List<ResponseStats> getStatsReport(Language language) {
    List<ResponseStats> stats = new ArrayList<>();
    bundleService.getAll().forEach(bundle -> {
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

    trackService.getAll().forEach(track -> {
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
        ,eventService.getCurrentEvent().getName()
        ,registrationService.countSubmittedConfirmingAndDone()
        ,registrationService.countConfirmingAndDone()
        ,registrationService.countDone()
       ,eventService.getCurrentEvent().getCapacity()
      )
    );

    slotService.getFoodSlotsPairs().forEach(foodSlot -> {
      Food food = (Food) foodSlot.get(0);
      Slot slot = (Slot) foodSlot.get(1);
      stats.add(

        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(food.getName(), language.getLanguageKey()).getOutText()
            + " ("
            + outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText()
            + ")"
          , foodRegistrationService.countFoodSlotSubmittedReleasedAndDone(food, slot)
          , foodRegistrationService.countFoodSlotConfirmingAndDone(food, slot)
          , foodRegistrationService.countFoodSlotDone(food, slot)
          , null
        )
      );
    });

    discountService.findAll().forEach(discount -> {
      stats.add(
        new ResponseStats(
          "Discount"
          ,outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countDiscountSubmittedConfirmingAndDone(discount)
          , discountRegistrationService.countDiscountConfirmingAndDone(discount)
          , discountRegistrationService.countDiscountDone(discount)
          , discount.getCapacity()
        )
      );
    });
    return stats;
  }
}
