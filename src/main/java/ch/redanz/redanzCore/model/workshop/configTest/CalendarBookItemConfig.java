package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.config.BookItemType;
import ch.redanz.redanzCore.model.workshop.entities.EventCalendarBookItem;
import ch.redanz.redanzCore.model.workshop.service.BundleService;
import ch.redanz.redanzCore.model.workshop.service.CalendarService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@Getter
@AllArgsConstructor
public enum CalendarBookItemConfig {
  PARTY_PASS_FRI(CalendarConfig.PARTY_FRI, BundleConfig.PARTY_PASS, BookItemType.BUNDLE),
  PARTY_PASS_SAT(CalendarConfig.PARTY_SAT, BundleConfig.PARTY_PASS, BookItemType.BUNDLE),
  PARTY_PASS_SUN(CalendarConfig.PARTY_SUN, BundleConfig.PARTY_PASS, BookItemType.BUNDLE),
  FULL_PASS_FRI(CalendarConfig.PARTY_FRI, BundleConfig.FULL_PASS, BookItemType.BUNDLE),
  FULL_PASS_CLASS_FRI(CalendarConfig.CLASS_FRI, BundleConfig.FULL_PASS, BookItemType.BUNDLE);

  private final CalendarConfig calendarConfig;
  private final BundleConfig bundleConfig;
  private final BookItemType bookItemType;

  public static void setup(BundleService bundleService, CalendarService calendarService) {
    for (CalendarBookItemConfig calendarBookItemConfig : CalendarBookItemConfig.values()) {
      calendarService.save(
        new EventCalendarBookItem(
          calendarService.findByTitle(calendarBookItemConfig.getCalendarConfig().getTitle()),
          bundleService.findByName(calendarBookItemConfig.bundleConfig.getName()).getBundleId(),
          calendarBookItemConfig.bookItemType
        )
      );
    }
  }
}
