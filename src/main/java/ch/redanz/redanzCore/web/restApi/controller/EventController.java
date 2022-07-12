package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import ch.redanz.redanzCore.model.workshop.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("core-api/app/event")
@AllArgsConstructor
public class EventController {
  private final EventService eventService;
  private final SlotService slotService;
  private final OutTextService outTextService;
  private final AccommodationService accommodationService;
  private final SpecialService specialService;

  @GetMapping(path = "/all")
  public List<Event> getAllEvents() {
    return eventService.getAllEvents();
  }

  @GetMapping(path = "/current")
  public Event getCurrentEvent() {
    return eventService.getCurrentEvent();
  }

  @GetMapping(path = "/out-text/all")
  public HashMap getOutText() {
    ArrayList<String> types = new ArrayList<>() {
      {
        add("FRONT_LOGIN");
        add("FRONT_BASE");
      }
    };

    return outTextService.getOutTextByType(types);
  }

  @GetMapping(path = "/volunteer/slot/all")
  public List<Slot> getAllVolunteerSlots() {
    return slotService.getAllVolunteerSlots();
  }

  @GetMapping(path = "/accommodation/all")
  public AccommodationResponse getAccommodationSlots() {
    return accommodationService.getAll();
  }

  @GetMapping(path = "/special/all")
  public List<Special> getSpecials() {
    return specialService.findAll();
  }
}
