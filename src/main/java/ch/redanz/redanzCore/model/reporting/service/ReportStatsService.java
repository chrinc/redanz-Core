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

    // Event Specials
    event.getEventSpecials().forEach(eventSpecial -> {
      if (!eventSpecial.getInfoOnly()) {
        Special special = eventSpecial.getSpecial();
        stats.add(
          new ResponseStats(
            "Special"
            , outTextService.getOutTextByKeyAndLangKey(special.getName(), language.getLanguageKey()).getOutText()
            , specialRegistrationService.countSpecialRegistrationsAndSplitRoles(special, event)
            , specialRegistrationService.countSpecialsSubmittedAndSplitRoles(special, event)
            , specialRegistrationService.countSpecialsConfirmingAndSplitRoles(special, event)
            , specialRegistrationService.countSpecialsDoneAndSplitRoles(special, event)
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
          , outTextService.getOutTextByKeyAndLangKey(eventFoodSlot.getSlot().getName(), language.getLanguageKey()).getOutText()
          , foodRegistrationService.countFoodSlotSubmittedConfirmingAndDoneAsList(eventFoodSlot.getFood(), eventFoodSlot.getSlot(), event)
          , foodRegistrationService.countFoodSlotSubmittedAsList(eventFoodSlot.getFood(), eventFoodSlot.getSlot(), event)
          , foodRegistrationService.countFoodSlotConfirmingAsList(eventFoodSlot.getFood(), eventFoodSlot.getSlot(), event)
          , foodRegistrationService.countFoodSlotDoneAsList(eventFoodSlot.getFood(), eventFoodSlot.getSlot(), event)
          , null
          , no
        )
      );
    });

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
          , eventDiscount.getCapacity() == null ? no
            : discountRegistrationService.countDiscountSubmittedConfirmingAndDone(discount, event) >= eventDiscount.getCapacity() ? yes : no
        )
      );
    });
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
            ,eventPrivateClass.getSoldOut() ? yes : no
          )
        );
      });
    return stats;
  }
}
