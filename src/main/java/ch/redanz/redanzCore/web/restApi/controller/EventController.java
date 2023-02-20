package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.registration.response.RegistrationResponse;
import ch.redanz.redanzCore.model.workshop.config.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.Event;
import ch.redanz.redanzCore.model.workshop.entities.Slot;
import ch.redanz.redanzCore.model.workshop.entities.Special;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import ch.redanz.redanzCore.model.workshop.service.*;
import ch.redanz.redanzCore.web.security.exception.ApiRequestException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  @GetMapping(path = "/schema/event")
  public List<Map<String, String>> getEventSchema() {
    return eventService.getEventSchema();
  }

  @GetMapping(path = "/data/event")
  public List<Map<String, String>> getEventData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getEventsData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/data/event/all")
  public List<Map<String, String>> getAllEventData() {
    return eventService.getEventsData(eventService.getAllEvents());
  }

  @GetMapping(path = "/schema/bundle")
  public List<Map<String, String>> getBundleSchema() {
    return eventService.getBundleSchema();
  }

  @GetMapping(path = "/data/bundle")
  public List<Map<String, String>> getBundleData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getBundlesData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/data/bundle/all")
  public List<Map<String, String>> getAllBundleData() {
    return eventService.getBundlesData(eventService.getAllEvents());
  }

  @GetMapping(path = "/schema/track")
  public List<Map<String, String>> getTrackSchema() {
    return eventService.getTrackSchema();
  }

  @GetMapping(path = "/data/track")
  public List<Map<String, String>> getTrackData(
    @RequestParam("eventId") Long eventId
  ) {
    return eventService.getTracksData(new ArrayList<>(){{ add(eventService.findByEventId(eventId)); }});
  }

  @GetMapping(path = "/data/track/all")
  public List<Map<String, String>> getAllTrackData() {
    return eventService.getTracksData(eventService.getAllEvents());
  }


  @GetMapping(path = "/all")
  public List<Event> getAllEvents() {
    return eventService.getAllEvents();
  }

  @GetMapping(path = "/byId")
  @Transactional
  public Event getUserActiveRegistration(
    @RequestParam("eventId") Long eventId
  ) {
    try {
      return eventService.getById(eventId);
    } catch (Exception exception) {
      throw new ApiRequestException(OutTextConfig.LABEL_ERROR_UNEXPECTED_EN.getOutTextKey());
    }
  }

  @GetMapping(path = "/current")
  public Event getCurrentEvent() {
    return eventService.getCurrentEvent();
  }

  @GetMapping(path = "/active")
  public List<Event> getActivEvents() {
    return eventService.getActiveEvents();
  }

  @GetMapping(path = "/inactive")
  public List<Event> getInactiveEvents() {
    return eventService.getInactiveEvents();
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
    return accommodationService.getResponse();
  }

  @GetMapping(path = "/special/all")
  public List<Special> getSpecials() {
    return specialService.findAll();
  }
}
