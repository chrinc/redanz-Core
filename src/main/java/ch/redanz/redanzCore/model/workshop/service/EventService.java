package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.config.EventConfig;
import ch.redanz.redanzCore.model.workshop.repository.BundleEventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventRepo;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class EventService {
  private final EventRepo eventRepo;
  private final BundleEventRepo bundleEventRepo;
  private final EventTypeSlotRepo eventTypeSlotRepo;

  public void save(Event event) {
    eventRepo.save(event);
  }
  public void save(EventBundle eventBundle) {
    bundleEventRepo.save(eventBundle);
  }
  public void save (EventTypeSlot eventTypeSlot) {
    this.eventTypeSlotRepo.save(eventTypeSlot);
  }
  public List<Event> getAllEvents() {
    return eventRepo.findAllByArchived(false);
  }
  public Event getCurrentEvent() {
    return eventRepo.findByName(EventConfig.EVENT2022.getName());
  }
  public Event getById(Long eventId) {
    return eventRepo.findByEventId(eventId);
  }
  public List<Event> getActiveEvents() {
    return eventRepo.findAllByActiveAndArchived(true, false);
  }
  public List<Event> getInactiveEvents() {
    return eventRepo.findAllByActiveAndArchived(false, false);
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

  public List<Map<String, String>> getEventSchema(){
    return Event.schema();
  }
  public List<Map<String, String>> getEventsData(List<Event> eventList){
    List<Map<String, String>> eventData = new ArrayList<>();
    eventList.forEach(event -> {
      eventData.add(event.dataMap());
    });
    return eventData;
  }

  public List<Map<String, String>> getBundleSchema(){
    return Bundle.schema();
  }
  public List<Map<String, String>> getBundlesData(List<Event> eventList){
    List<Map<String, String>> bundlesData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getEventBundles().forEach(eventBundle -> {
        // bundle data
        Map<String, String> bundleData = eventBundle.getBundle().dataMap();

        // add event info
        bundleData.put("eventId", Long.toString(event.getEventId()));

        bundlesData.add(bundleData);
      });
    });
    return bundlesData;
  }
  public List<Map<String, String>> getTrackSchema(){
    return Track.schema();
  }
  public List<Map<String, String>> getTracksData(List<Event> eventList){
    log.info("inc@getTracksData, getTracksData");
    List<Map<String, String>> tracksData = new ArrayList<>();
    eventList.forEach(event -> {
      event.getEventBundles().forEach(eventBundle -> {
        log.info("inc@getTracksData, bundleName: " + eventBundle.getBundle().getName());
        eventBundle.getBundle().getBundleTracks().forEach(

          bundleTrack -> {
            log.info("inc@getTracksData, trackName: " + bundleTrack.getTrack().getName());
            Map<String, String> trackData = bundleTrack.getTrack().dataMap();
            log.info("inc@getTracksData, trackData.size 1: " + trackData.size());
            trackData.put("eventId", Long.toString(event.getEventId()));
            log.info("inc@getTracksData, trackData.size 2: " + trackData.size());
            trackData.put("bundleId", Long.toString(eventBundle.getBundle().getBundleId()));
            log.info("inc@getTracksData, trackData.size 3: " + trackData.size());
            tracksData.add(trackData);
            log.info("inc@getTracksData, tracksData.size: " + tracksData.size());
          });
      });
    });
    return tracksData;
  }
}
