package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ReportStatsService {
  private final RegistrationService registrationService;
  private final BundleService bundleService;
  private final OutTextService outTextService;
  private final FoodRegistrationService foodRegistrationService;
  private final DiscountRegistrationService discountRegistrationService;
  private final SpecialService specialService;
  private final SpecialRegistrationService specialRegistrationService;
  private final FoodService foodService;
  private final EventService eventService;
  private final TrackService trackService;
  public List<ResponseStats> getStatsReport(Language language, Event event) {
    List<ResponseStats> stats = new ArrayList<>();

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

    // EventSpecials
    event.getEventSpecials().forEach(eventSpecial -> {
      Special special = eventSpecial.getSpecial();
      stats.add(
        new ResponseStats(
          "Special"
          ,outTextService.getOutTextByKeyAndLangKey(special.getName(), language.getLanguageKey()).getOutText()
         ,specialRegistrationService.countSpecialRegistrationsAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsSubmittedAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsConfirmingAndSplitRoles(special, event)
         ,specialRegistrationService.countSpecialsDoneAndSplitRoles(special, event)
         ,eventSpecial.getCapacity()
        )
      );
    });

    // BundleSpecials
//    Set<Special> specials = new HashSet<>();
//    event.getEventBundles().forEach(eventBundle -> {
//      eventBundle.getBundle().getBundleSpecials().forEach(
//        bundleSpecial -> {
//          specials.add(bundleSpecial.getSpecial());
//        });
//      });
//
//    AtomicInteger specialCapacity = new AtomicInteger();
//    specials.forEach(
//      special -> {
//        eventService.findBundleSpecialsByEventAndSpecial(event, special).forEach(bundleSpecial -> {
//          specialCapacity.addAndGet(bundleSpecial.getCapacity());
//        });
//
//      stats.add(
//        new ResponseStats(
//          "Special"
//          ,outTextService.getOutTextByKeyAndLangKey(special.getName(), language.getLanguageKey()).getOutText()
//          ,specialRegistrationService.countSpecialRegistrationsAndSplitRoles(special, event)
//          ,specialRegistrationService.countSpecialsSubmittedAndSplitRoles(special, event)
//          ,specialRegistrationService.countSpecialsConfirmingAndSplitRoles(special, event)
//          ,specialRegistrationService.countSpecialsDoneAndSplitRoles(special, event)
//          ,specialCapacity.get()
//        )
//      );
//    });

    // Private Classes
    event.getEventPrivates().forEach(
      eventPrivateClass -> {
      PrivateClass privateClass = eventPrivateClass.getPrivateClass();
      stats.add(
        new ResponseStats(
          "Private Class"
          ,privateClass.getName()
          ,specialRegistrationService.countPrivateRegistrationsAndSplitRoles(privateClass, event)
          ,specialRegistrationService.countPrivatesSubmittedAndSplitRoles(privateClass, event)
          ,specialRegistrationService.countPrivatesConfirmingAndSplitRoles(privateClass, event)
          ,specialRegistrationService.countPrivatesDoneAndSplitRoles(privateClass, event)
         ,eventPrivateClass.getCapacity()
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

//    // Track Discounts
//    Set<Discount> discounts = new HashSet<>();
//    event.getEventBundles().forEach(eventBundle -> {
//        if (trackService.bundleHasTrack(eventBundle.getBundle())) {
//          eventBundle.getBundle().getBundleTracks().forEach(bundleTrack -> {
//            bundleTrack.getTrack().getEventDiscounts().forEach(eventDiscount -> {
//              Discount discount = eventDiscount.getDiscount();
//              discounts.add(discount);
//            });
//          });
//        }
//      });
//      event.getEventDiscounts().forEach(eventDiscount -> {
//        Discount discount = eventDiscount.getDiscount();
//        stats.add(
//          new ResponseStats(
//            "Discount"
//            , outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
//            , discountRegistrationService.countDiscountSubmittedConfirmingAndDoneAsList(discount, event)
//            , discountRegistrationService.countDiscountSubmittedAsList(discount, event)
//            , discountRegistrationService.countDiscountConfirmingAsList(discount, event)
//            , discountRegistrationService.countDiscountDoneAsList(discount, event)
//            , eventDiscount.getCapacity()
//          )
//        );
//      });

    // Track Discounts
    Set<Discount> discounts = new HashSet<>();
    event.getEventTracks().forEach(eventTrack -> {
      eventTrack.getTrack().getEventDiscounts().forEach(eventDiscount -> {
        Discount discount = eventDiscount.getDiscount();
        discounts.add(discount);
      });
    });
    event.getEventDiscounts().forEach(eventDiscount -> {
      Discount discount = eventDiscount.getDiscount();
      stats.add(
        new ResponseStats(
          "Discount"
          , outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countDiscountSubmittedConfirmingAndDoneAsList(discount, event)
          , discountRegistrationService.countDiscountSubmittedAsList(discount, event)
          , discountRegistrationService.countDiscountConfirmingAsList(discount, event)
          , discountRegistrationService.countDiscountDoneAsList(discount, event)
          , eventDiscount.getCapacity()
        )
      );
    });
    return stats;
  }
}
