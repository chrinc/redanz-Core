package ch.redanz.redanzCore.web.restApi.controller;


import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.Slot;
import ch.redanz.redanzCore.model.workshop.response.AccommodationResponse;
import ch.redanz.redanzCore.model.workshop.service.AccommodationService;
import ch.redanz.redanzCore.model.workshop.service.EventService;
import ch.redanz.redanzCore.model.workshop.service.SlotService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("core-api/app/event")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;
    private final SlotService slotService;
    private final AccommodationService accommodationService;

    @GetMapping(path="/all")
    public List<Event> getAllEvents() {
        log.info("inc, send getAllTracks: {}.", eventService.getAllEvents());
        return eventService.getAllEvents();
    }

    @GetMapping(path="/current")
    public Event getCurrentEvent() {
        log.info("inc, send getAllTracks: {}.", eventService.getCurrentEvent());
        return eventService.getCurrentEvent();
    }

    @GetMapping(path="/out-text/all")
    public HashMap getOutText() {
        log.info("inc, send getOutText: {}.", eventService.getAllOutText());
        return eventService.getAllOutText();
    }
    
    @GetMapping(path="/volunteer/slot/all")
    public List<Slot> getAllVolunteerSlots() {
        log.info("inc, send getOutText: {}.", slotService.getAllVolunteerSlots());
        return slotService.getAllVolunteerSlots();
    }

    @GetMapping(path="/accommodation/all")
    public AccommodationResponse getAccommodationSlots() {
        log.info("inc, send getOutText: {}.", accommodationService.getAll());
        return accommodationService.getAll();
    }
}
