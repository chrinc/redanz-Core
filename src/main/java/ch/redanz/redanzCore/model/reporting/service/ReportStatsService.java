package ch.redanz.redanzCore.model.reporting.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.registration.service.DiscountRegistrationService;
import ch.redanz.redanzCore.model.registration.service.FoodRegistrationService;
import ch.redanz.redanzCore.model.registration.service.RegistrationService;
import ch.redanz.redanzCore.model.reporting.response.ResponseStats;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
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
  private final FoodService foodService;
  private final FoodRegistrationService foodRegistrationService;
  private final DiscountService discountService;
  private final DiscountRegistrationService discountRegistrationService;
  public List<ResponseStats> getStatsReport(Language language) {
    List<ResponseStats> stats = new ArrayList<>();
    log.info("inc, start stats, stats: {}", stats);
    log.info("inc, start stats, language: {}", language.getLanguageKey());
    bundleService.getAll().forEach(bundle -> {
      stats.add(
        new ResponseStats(
          "Pass"
          ,bundle.getName()
         ,registrationService.countBundlesReleasedAndDone(bundle)
         ,bundle.getCapacity()
        )
      );
    });

    log.info("inc, start stats, after Bundles: {}", stats);
    trackService.getAll().forEach(track -> {
      stats.add(
        new ResponseStats(
          "Track"
          ,track.getName()
         ,registrationService.countTracksReleasedAndDone(track)
         ,track.getCapacity()
        )
      );
    });

    log.info("inc, start stats, after Tracks: {}", stats);
    stats.add(
      new ResponseStats(
        "Workshop"
        ,eventService.getCurrentEvent().getName()
       ,registrationService.countReleasedAndDone()
       ,eventService.getCurrentEvent().getCapacity()
      )
    );

    foodService.findAll().forEach(food -> {
      stats.add(
        new ResponseStats(
          "Food"
          ,outTextService.getOutTextByKeyAndLangKey(food.getName(), language.getLanguageKey()).getOutText()
          , foodRegistrationService.countFoodReleasedAndDone(food)
          , null
        )
      );
    });

    discountService.findAll().forEach(discount -> {
      stats.add(
        new ResponseStats(
          "Discount"
          ,outTextService.getOutTextByKeyAndLangKey(discount.getName(), language.getLanguageKey()).getOutText()
          , discountRegistrationService.countDiscountReleasedAndDone(discount)
          , discount.getCapacity()
        )
      );
    });
    return stats;
  }
}
