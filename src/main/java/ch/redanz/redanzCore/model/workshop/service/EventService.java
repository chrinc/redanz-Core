package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.registration.Registration;
import ch.redanz.redanzCore.model.reporting.ResponsePerson;
import ch.redanz.redanzCore.model.workshop.Event;
import ch.redanz.redanzCore.model.workshop.OutText;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.OutTextRepo;
import ch.redanz.redanzCore.model.workshop.response.OutTextResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class EventService {
    private final EventRepo eventRepo;
    private final OutTextRepo outTextRepo;

    public List<Event> getAllEvents() { return eventRepo.findAll(); }

    public Event getCurrentEvent() { return eventRepo.findByName(EventConfig.EVENT2022.getName());}
    public HashMap getAllOutText() {
        HashMap outTextMap = new HashMap();

        outTextRepo.findAll().forEach(outText -> {
            outTextMap.put(
                    outText.getOutTextId().getOutTextKey()  + "." + outText.getOutTextId().getOutTextLanguageKey(),
                    outText.getOutText()
            );
        });

        return outTextMap;
    }


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
