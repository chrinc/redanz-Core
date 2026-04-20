package ch.redanz.redanzCore.model.workshop.configTest;

import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.VolunteerType;
import ch.redanz.redanzCore.model.workshop.service.EventService;
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
public enum EventVolunteerTypeConfig {
  EVENT_TWO_HOURS(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_VOL_TYPE_FOUR_HOURS_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_FOUR_HOURS_DESC_EN.getOutTextKey()),
  EVENT_FIVE_HOURS(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_VOL_TYPE_FIVE_HOURS_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_FIVE_HOURS_DESC_EN.getOutTextKey()),
  EVENT_NO_PREF_HOURS(EventConfig.REDANZ_EVENT, OutTextConfig.LABEL_VOL_TYPE_NO_PREF_NAME_EN.getOutTextKey(), OutTextConfig.LABEL_VOL_TYPE_NO_PREF_DESC_EN.getOutTextKey());

  private final EventConfig eventConfig;
  private final String name;
  private final String description;


  public static void setup(EventService eventService) {
    for (EventVolunteerTypeConfig eventVolunteerTypeConfig : EventVolunteerTypeConfig.values()) {
      Event event = eventService.findByName(eventVolunteerTypeConfig.getEventConfig().getName());
      eventService.save(new VolunteerType(eventVolunteerTypeConfig.getName(), eventVolunteerTypeConfig.getDescription(), event));
    }
  }
}
