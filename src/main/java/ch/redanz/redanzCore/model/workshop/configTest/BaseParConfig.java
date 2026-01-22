package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.BasePar;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.service.BaseParService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@AllArgsConstructor
public enum BaseParConfig {
  DOAUTOMATCH("false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_AUTO_MATCH_EN.getOutTextKey()),
  DOAUTORELEASE("false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_AUTO_RELEASE_EN.getOutTextKey()),
  TESTMAILONLY( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_TEST_MAIL_ONLY_EN.getOutTextKey()),
  DOEODCANCEL( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_CANCEL_EN.getOutTextKey()),
  DOEODMATCHING( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_MATCHING_EN.getOutTextKey()),
  DOEODRELEASE( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_RELEASE_EN.getOutTextKey()),
  DOEODREMINDER( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_REMINDER_EN.getOutTextKey()),
  REMINDERAFTERDAYS( "5", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_REMINDER_AFTER_DAYS_EN.getOutTextKey()),
  CANCELAFTERDAYS( "3", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_CANCEL_AFTER_DAYS_EN.getOutTextKey()),
  WAITLISTLENGTH( "2", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_WAIT_LIST_LENGTH_EN.getOutTextKey()),
;

  private final String val;
  private final EventConfig event;
  private final String name;

  public static void setup(BaseParService baseParService, EventService eventService) {

    for (BaseParConfig baseParConfig : BaseParConfig.values()) {
      if (!baseParService.existsByNameAndEvent(
        baseParConfig.getName(), eventService.findByName(baseParConfig.getEvent().getName()))
      )
      {
        baseParService.save(
          new BasePar(
            baseParConfig.val,
            baseParConfig.name,
            eventService.findByName(baseParConfig.getEvent().getName())
          )
        );
      }
    }
  }

  public static void setupDefault(BaseParService baseParService, Event event) {

    for (BaseParConfig baseParConfig : BaseParConfig.values()) {
      if (!baseParService.existsByNameAndEvent(
        baseParConfig.getName(), event)
      )
      {
        baseParService.save(
          new BasePar(
            baseParConfig.val,
            baseParConfig.name,
            event
          )
        );
      }
    }
  }
}
