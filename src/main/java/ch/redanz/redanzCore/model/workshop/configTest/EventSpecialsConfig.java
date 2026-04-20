package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.EventSpecial;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SpecialService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventSpecialsConfig {
  EVENT_MASSAGE(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_SPECIAL_CHINESE_MASSAGE_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_SPECIAL_CHINESE_MASSAGE_DESC_EN.getOutTextKey(), 45.0, false, 30, OutTextConfig.LABEL_SPECIALS_MASSAGE_URL_EN.getOutTextKey(), true),
  EVENT_MAGAZINE_DESIGN(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_SPECIAL_MAGAZINE_DESIGN_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_SPECIAL_MAGAZINE_DESIGN_DESC_EN.getOutTextKey(), 15.0, false, 30, null, false),
  EVENT_BRUNCH(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_SPECIAL_BRUNCH_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_SPECIAL_BRUNCH_DESC_EN.getOutTextKey(), 120.0, false, 30, null, false)
  ;

  private final EventConfig eventConfig;
  private final String name;
  private final String description;
  private final double price;
  private final boolean soldOut;
  private final int capacity;
  private final String url;
  private final Boolean infoOnly;

  public static void setup(SpecialService specialService, EventService eventService) {
    for (EventSpecialsConfig eventSpecialsConfig : EventSpecialsConfig.values()) {
      Event event = eventService.findByName(eventSpecialsConfig.eventConfig.getName());
      eventService.save(new EventSpecial(
         eventSpecialsConfig.name
        ,eventSpecialsConfig.description
        ,event
        ,eventSpecialsConfig.price
        ,eventSpecialsConfig.soldOut
        ,eventSpecialsConfig.capacity
        ,eventSpecialsConfig.url
        ,eventSpecialsConfig.infoOnly
      ));
    }
  }
}
