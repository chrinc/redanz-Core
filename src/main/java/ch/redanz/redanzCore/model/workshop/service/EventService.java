package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepo eventRepo;

//    @Autowired
//    public EventService(EventRepo eventRepo) {
//        this.eventRepo = eventRepo;
//    }

    public Event findByEventId(Long eventId) {
        return eventRepo.findByEventId(eventId);
    }
    public Event findByName(String name) {
        return eventRepo.findByName(name);
    }
    public List<Event> findAll() {
        return eventRepo.findAll();
    }
}
