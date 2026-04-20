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
  DOAUTOMATCH("false", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_AUTO_MATCH_EN.getOutTextKey(), "bool"),
  DOAUTORELEASE("false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_AUTO_RELEASE_EN.getOutTextKey(), "bool"),
  TESTMAILONLY( "true", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_TEST_MAIL_ONLY_EN.getOutTextKey(), "bool"),
  DOEODCANCEL( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_CANCEL_EN.getOutTextKey(),"bool"),
  DOEODMATCHING( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_MATCHING_EN.getOutTextKey(),"bool"),
  DOEODRELEASE( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_RELEASE_EN.getOutTextKey(),"bool"),
  DOEODREMINDER( "false", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_EOD_REMINDER_EN.getOutTextKey(),"bool"),
  REMINDERAFTERDAYS( "5", EventConfig.REDANZ_EVENT,OutTextConfig.LABEL_BASE_PAR_REMINDER_AFTER_DAYS_EN.getOutTextKey(), "number"),
  CANCELAFTERDAYS( "3", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_CANCEL_AFTER_DAYS_EN.getOutTextKey(),"number"),
  WAITLISTLENGTH( "2", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_WAIT_LIST_LENGTH_EN.getOutTextKey(), "number"),
  ORGANIZERNAME( "Redanz", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_ORGANIZER_NAME_EN.getOutTextKey(), "string"),
  BOOKLETREADY( "[{\"EN\":\"https://www.redanz.ch/booklet_en.pdf\",\"GE\":\"https://www.redanz.ch/booklet_de.pdf\"}]", EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_BASE_PAR_BOOKLET_LINK_EN.getOutTextKey(),"label"),
;

  private final String val;
  private final EventConfig eventConfig;
  private final String name;
  private final String type;

  public static void setup(BaseParService baseParService, EventService eventService) {

    for (BaseParConfig baseParConfig : BaseParConfig.values()) {
      if (!baseParService.existsByName(
        baseParConfig.getName())
      )
      {
        baseParService.save(
          new BasePar(
            baseParConfig.val,
            baseParConfig.name,
            eventService.findByName(baseParConfig.eventConfig.getName()),
            baseParConfig.type
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
            event,
            baseParConfig.type
          )
        );
      }
    }
  }
}
