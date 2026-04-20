package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.registration.service.SpecialRegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
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
  private final SpecialRegistrationService specialRegistrationService;
  private final FoodService foodService;
  private final EventService eventService;
  private final BundleEventTrackService bundleEventTrackService;
  public List<ResponseStats> getStatsReport(Language language, Event event) {
    String yes = outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_YES_EN.getOutTextKey(), language.getLanguageKey()).getOutText();
    String no = outTextService.getOutTextByKeyAndLangKey(OutTextConfig.LABEL_NO_EN.getOutTextKey(), language.getLanguageKey()).getOutText();
    List<ResponseStats> stats = new ArrayList<>();
    stats.add(
      new ResponseStats(
        "Workshop"
        ,event.getName()
        ,registrationService.countSubmittedConfirmingAndDoneAndSplitRoles(event)
        ,registrationService.countSubmittedAndSplitRoles(event)
        ,registrationService.countConfirmingAndSplitRoles(event)
        ,registrationService.countDoneAndSplitRoles(event)
        ,event.getCapacity()
        ,event.isSoldOut() ? yes : no
      )
    );

    eventService.findAllEventBundles(event).forEach(eventBundle -> {
      List<BundleEventTrack> bundleEventTracks = bundleEventTrackService.findAllByEventAndBundle(event, eventBundle.getBundle());
      bundleEventTracks.forEach(
        bundleEventTrack -> {
          Track track = bundleEventTrack.getEventTrack().getTrack();
          Bundle bundle = bundleEventTrack.getBundle();
          stats.add(
            new ResponseStats(
              bundleEventTrack.getBundle().getName()
              , track.getName()
              , registrationService.countTracksByBundleSubmittedConfirmingAndDoneAndSplitRoles(track, bundle, event)
              , registrationService.countTracksByBundleSubmittedAndSplitRoles(track, bundle, event)
              , registrationService.countTracksByBundleConfirmingAndSplitRoles(track, bundle, event)
              , registrationService.countTracksByBundleDoneAndSplitRoles(track, bundle, event)
              , bundleEventTrack.getCapacity()
              , eventBundle.isSoldOut() || bundleEventTrack.isSoldOut() ? yes : no
            )
          );
        });
    });

    bundleService.getAllWithoutTracksByEvent(event).forEach(bundle -> {
      stats.add(
        new ResponseStats(
          "Simple Pass"
          ,bundle.getName()
          ,registrationService.countBundlesSubmittedConfirmingAndDoneAndSplitRoles(bundle, event)
          ,registrationService.countBundlesSubmittedAndSplitRoles(bundle, event)
          ,registrationService.countBundlesConfirmingAndSplitRoles(bundle, event)
          ,registrationService.countBundlesDoneAndSplitRoles(bundle, event)
          ,eventService.findByEventAndBundle(event, bundle).getCapacity()
          ,eventService.findByEventAndBundle(event, bundle).isSoldOut() ? yes : no
        )
      );
    });

    // Event
    event.getEventSpecials().forEach(eventSpecial -> {
      if (!eventSpecial.getInfoOnly()) {
        stats.add(
          new ResponseStats(
            "Special"
            , outTextService.getOutTextByKeyAndLangKey(eventSpecial.getName(), language.getLanguageKey()).getOutText()
            , specialRegistrationService.countEventSpecialRegistrationsAndSplitRoles(eventSpecial, event)
            , specialRegistrationService.countEventSpecialsSubmittedAndSplitRoles(eventSpecial, event)
            , specialRegistrationService.countEventSpecialsConfirmingAndSplitRoles(eventSpecial, event)
            , specialRegistrationService.countEventSpecialsDoneAndSplitRoles(eventSpecial, event)
            , eventSpecial.getCapacity()
            , eventSpecial.getSoldOut() ? yes : no
          )
        );
      }
    });

    foodService.getEventFoodSlots(event).forEach(eventFoodSlot -> {
      stats.add(
        new ResponseStats(
          "Food"
          , outTextService.getOutTextByKeyAndLangKey(eventFoodSlot.getName(), language.getLanguageKey()).getOutText()
          , foodRegistrationService.countFoodSlotSubmittedConfirmingAndDoneAsList(eventFoodSlot, event)
          , foodRegistrationService.countFoodSlotSubmittedAsList(eventFoodSlot, event)
          , foodRegistrationService.countFoodSlotConfirmingAsList(eventFoodSlot, event)
          , foodRegistrationService.countFoodSlotDoneAsList(eventFoodSlot, event)
          , null
          , no
        )
      );
    });

    // Track Discounts
//    Set<EventDiscount> eventDiscounts = new HashSet<>();
//    event.getEventTracks().forEach(eventTrack -> {
//      eventTrack.getTrack().getEventDiscounts().forEach(eventDiscount -> {
//        eventDiscounts.add(eventDiscount);
//      });
//    });
    event.getEventDiscounts().forEach(eventDiscount -> {
      stats.add(
        new ResponseStats(
          "Discount"
          , outTextService.getOutTextByKeyAndLangKey(eventDiscount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countEventDiscountSubmittedConfirmingAndDoneAsList(eventDiscount)
          , discountRegistrationService.countEventDiscountSubmittedAsList(eventDiscount)
          , discountRegistrationService.countEventDiscountConfirmingAsList(eventDiscount)
          , discountRegistrationService.countEventDiscountDoneAsList(eventDiscount)
          , eventDiscount.getCapacity()
          , eventDiscount.getCapacity() == null ? no
            : discountRegistrationService.countEventDiscountSubmittedConfirmingAndDone(eventDiscount) >= eventDiscount.getCapacity() ? yes : no
        )
      );
    });
    // Private Classes
    event.getEventPrivates().forEach(
      eventPrivateClass -> {
        stats.add(
          new ResponseStats(
            "Private Class"
            ,eventPrivateClass.getName()
            ,specialRegistrationService.countPrivateRegistrationsAndSplitRoles(eventPrivateClass)
            ,specialRegistrationService.countPrivatesSubmittedAndSplitRoles(eventPrivateClass)
            ,specialRegistrationService.countPrivatesConfirmingAndSplitRoles(eventPrivateClass)
            ,specialRegistrationService.countPrivatesDoneAndSplitRoles(eventPrivateClass)
            ,eventPrivateClass.getCapacity()
            ,eventPrivateClass.getSoldOut() ? yes : no
          )
        );
      });
    return stats;
  }
}
