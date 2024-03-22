package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
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
  private final FoodService foodService;
  private final EventService eventService;

  public List<ResponseStats> getStatsReport(Language language, Event event) {
    List<ResponseStats> stats = new ArrayList<>();
    // log.info("event EventId:" + event.getEventId());

    bundleService.getAllByEvent(event).forEach(bundle -> {
      stats.add(
        new ResponseStats(
          "Pass"
          ,bundle.getName()
          ,registrationService.countBundlesSubmittedConfirmingAndDoneAndSplitRoles(bundle, event)
          ,registrationService.countBundlesSubmittedAndSplitRoles(bundle, event)
          ,registrationService.countBundlesConfirmingAndSplitRoles(bundle, event)
          ,registrationService.countBundlesDoneAndSplitRoles(bundle, event)
           ,bundle.getCapacity()
        )
      );
    });
    // log.info("bundles: " );
    eventService.getAllTracksByEvent(event).forEach(track -> {
      stats.add(
        new ResponseStats(
          "Track"
          ,track.getName()
         ,registrationService.countTracksSubmittedConfirmingAndDoneAndSplitRoles(track, event)
         ,registrationService.countTracksSubmittedAndSplitRoles(track, event)
         ,registrationService.countTracksConfirmingAndSplitRoles(track, event)
         ,registrationService.countTracksDoneAndSplitRoles(track, event)
         ,track.getCapacity()
        )
      );
    });

    specialService.findByEventOrBundle(event).forEach(special -> {
      stats.add(
        new ResponseStats(
          "Special"
          ,outTextService.getOutTextByKeyAndLangKey(special.getName(), language.getLanguageKey()).getOutText()
         ,specialRegistrationService.countSpecialRegistrationsAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsSubmittedAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsConfirmingAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsDoneAndSplitRoles(special, event)
         ,special.getCapacity()
        )
      );
    });

    stats.add(
      new ResponseStats(
        "Workshop"
        ,event.getName()
        ,registrationService.countSubmittedConfirmingAndDoneAndSplitRoles(event)
        ,registrationService.countSubmittedAndSplitRoles(event)
        ,registrationService.countConfirmingAndSplitRoles(event)
        ,registrationService.countDoneAndSplitRoles(event)
       ,event.getCapacity()
      )
    );
    foodService.getFoodSlotsPairsByEvent(event).forEach(foodSlot -> {
      Food food = (Food) foodSlot.get(0);
      Slot slot = (Slot) foodSlot.get(1);

      // log.info("foodSlot, food" + ((Food) foodSlot.get(0)).getName());
      // log.info("foodSlot, slot" + ((Slot) foodSlot.get(1)).getName());
      stats.add(
        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText()
          , foodRegistrationService.countFoodSlotSubmittedConfirmingAndDoneAsList(food, slot, event)
          , foodRegistrationService.countFoodSlotSubmittedAsList(food, slot, event)
          , foodRegistrationService.countFoodSlotConfirmingAsList(food, slot, event)
          , foodRegistrationService.countFoodSlotDoneAsList(food, slot, event)
          , null
        )
      );
    });

    discountService.findAllByEvent(event).forEach(discount -> {
      stats.add(
        new ResponseStats(
          "Discount"
          ,outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countDiscountSubmittedConfirmingAndDoneAsList(discount, event)
          , discountRegistrationService.countDiscountSubmittedAsList(discount, event)
          , discountRegistrationService.countDiscountConfirmingAsList(discount, event)
          , discountRegistrationService.countDiscountDoneAsList(discount, event)
          , discount.getCapacity()
        )
      );
    });
    return stats;
  }
}
