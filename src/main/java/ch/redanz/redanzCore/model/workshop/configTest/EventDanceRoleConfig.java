package ch.redanz.redanzCore.model.workshop.configTest;

//import ch.redanz.redanzCore.model.workshop.entities.EventDiscount;
import ch.redanz.redanzCore.model.workshop.config.DanceRoleConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.service.DanceRoleService;
import ch.redanz.redanzCore.model.workshop.service.DiscountService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@AllArgsConstructor
public enum EventDanceRoleConfig {
  EVENT_SWITCH(EventConfig.REDANZ_EVENT, DanceRoleConfig.SWITCH, OutTextConfig.LABEL_SWITCH_LEVEL_HINT_EN.getOutTextKey()),
  EVENT_FOLLOW(EventConfig.REDANZ_EVENT, DanceRoleConfig.FOLLOWER, null),
  EVENT_LEAD(EventConfig.REDANZ_EVENT, DanceRoleConfig.LEADER, null);

  private final EventConfig eventConfig;
  private final DanceRoleConfig danceRoleConfig;
  private final String hint;

  public static void setup(DanceRoleService danceRoleService, EventService eventService) {
    for (EventDanceRoleConfig eventDanceRoleConfig : EventDanceRoleConfig.values()) {
      Event event = eventService.findByName(eventDanceRoleConfig.getEventConfig().getName());
      DanceRole danceRole = danceRoleService.findByName(eventDanceRoleConfig.danceRoleConfig.getName());
      eventService.save(new EventDanceRole(event, danceRole, eventDanceRoleConfig.hint));
    }
  }

}
