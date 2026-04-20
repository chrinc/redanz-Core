package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
@Getter
@AllArgsConstructor
public enum CalendarConfig {
  PARTY_FRI(EventConfig.REDANZ_EVENT, ZonedDateTime.parse("2026-11-06T21:00:00.000+01:00[Europe/Paris]"), ZonedDateTime.parse("2026-11-07T03:00:00.000+01:00[Europe/Paris]"), OutTextConfig.LABEL_CAL_TITLE_PARTY_FRI_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_DESC_PARTY_FRI_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_VENUE_PARTY_FRI_EN.getOutTextKey(), true),
  CLASS_FRI(EventConfig.REDANZ_EVENT, ZonedDateTime.parse("2026-11-06T11:00:00.000+01:00[Europe/Paris]"), ZonedDateTime.parse("2026-11-06T13:00:00.000+01:00[Europe/Paris]"), OutTextConfig.LABEL_CAL_TITLE_CLASS_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_DESC_CLASS_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_VENUE_PARTY_FRI_EN.getOutTextKey(), true),
  PARTY_SAT(EventConfig.REDANZ_EVENT, ZonedDateTime.parse("2026-11-07T21:00:00.000+01:00[Europe/Paris]"), ZonedDateTime.parse("2026-11-08T03:00:00.000+01:00[Europe/Paris]"), OutTextConfig.LABEL_CAL_TITLE_PARTY_SAT_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_DESC_PARTY_SAT_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_VENUE_PARTY_SAT_EN.getOutTextKey(), true),
  PARTY_SUN(EventConfig.REDANZ_EVENT, ZonedDateTime.parse("2026-11-08T21:00:00.000+01:00[Europe/Paris]"), ZonedDateTime.parse("2026-11-08T22:00:00.000+01:00[Europe/Paris]"), OutTextConfig.LABEL_CAL_TITLE_PARTY_SUN_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_DESC_PARTY_SUN_EN.getOutTextKey(), OutTextConfig.LABEL_CAL_VENUE_PARTY_SUN_EN.getOutTextKey(), true);

  private final EventConfig eventConfig;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String title;
  private String description;
  private String venue;
  private Boolean active;

  public static void setup(EventService eventService, CalendarService calendarService) {
    for (CalendarConfig calendarConfig : CalendarConfig.values()) {
      EventCalendar eventCalendar =
        new EventCalendar(
          eventService.findByName(calendarConfig.eventConfig.getName()),
          calendarConfig.startTime,
          calendarConfig.endTime,
          calendarConfig.title,
          calendarConfig.description,
          calendarConfig.venue,
          calendarConfig.active
        );
      calendarService.save(eventCalendar);
    }
  }
}
