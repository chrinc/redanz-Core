package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.profile.entities.Language;
import ch.redanz.redanzCore.model.workshop.config.SlotType;
import ch.redanz.redanzCore.model.workshop.configTest.OutTextConfig;
import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventSlotRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SlotService {
  private final EventSlotRepo eventSlotRepo;
  private final OutTextService outTextService;

  public boolean eventSlotExistsByName(String name) {
    return eventSlotRepo.existsByName(name);
  }
  public EventSlot findEventSlotByName(String name) {
    return eventSlotRepo.findByName(name);
  }

  public List<EventSlot> findAllByEvent (Event event){

    return eventSlotRepo.findAllByEvent(event);
  }
//  }

  public List<EventSlot> getAccommodationSlots(Event event) {
    return getAllEventSlots(SlotType.ACCOMMODATION, event);
  }
  public EventSlot findByEventSlotId(Long eventSlotId) {
    return eventSlotRepo.findByEventSlotId(eventSlotId);
  }
  public List<EventSlot> getAllEventSlots(Event event) {
    return eventSlotRepo.findAllByEvent(event);
  }
  public EventSlot findFirstByNameAndTypeAndEvent(String name, SlotType type, Event event) {
    return eventSlotRepo.findFirstByNameAndSlotTypeAndEvent(name, type, event);

  }

  public List<Map<String, String>> slotTypeMap() {
    return new ArrayList<>() {
      {
        add(new HashMap<>() {{put("name", SlotType.ACCOMMODATION.getLabel()); put("key", SlotType.ACCOMMODATION.name()); put("id", String.valueOf(SlotType.ACCOMMODATION.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.PARTY.getLabel()); put("key", SlotType.PARTY.name()); put("id", String.valueOf(SlotType.PARTY.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.VOLUNTEER.getLabel()); put("key", SlotType.VOLUNTEER.name()); put("id", String.valueOf(SlotType.VOLUNTEER.getCode()));}});
        add(new HashMap<>() {{put("name", SlotType.FOOD.getLabel()); put("key", SlotType.FOOD.name()); put("id", String.valueOf(SlotType.FOOD.getCode()));}});
      }
    };
  }

  public List<EventSlot> getAllEventSlots(SlotType type, Event event) {
    return eventSlotRepo.findAllByEventAndSlotType(event, type);
  }

  public List<Map<String, String>> eventSlotSchema(EventSlot eventSlot) {
    List<Map<String, String>> eventSlotSchema = EventSlot.schema();
    eventSlotSchema.forEach(item -> {
      if (item.get("key").equals("slotTypes")) {
        item.put("list", slotTypeMap().toString());
      }
    });
    return eventSlotSchema;
  }

  public List<Map<String, String>> eventSlotData(Event event, SlotType type) {
    List<Map<String, String>> eventSlotDataList = new ArrayList<>();
    getAllEventSlots(type, event).forEach(
      eventSlot -> {
        Map<String, String> eventSlotData = eventSlot.dataMap();
        eventSlotData.put("eventId", Long.toString(event.getEventId()));
        eventSlotDataList.add(eventSlotData);
      });
    return eventSlotDataList;
  }

  public List<Map<String, String>> eventSlotData(Event event) {
    List<Map<String, String>> eventSlotDataList = new ArrayList<>();
    getAllEventSlots(event).forEach(
      eventSlot -> {
        Map<String, String> eventSlotData = eventSlot.dataMap();
        eventSlotData.put("eventId", Long.toString(event.getEventId()));
        eventSlotDataList.add(eventSlotData);
      });
    return eventSlotDataList;
  }
  public String slotNames(List<EventSlot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }
    // Sort the list by seqNr before processing
    slots.sort(Comparator.comparing(EventSlot::getSeqNr));

    return slots.stream()
      .map(slot -> outTextService.getOutTextByKeyAndLangKey(slot.getName(), language.getLanguageKey()).getOutText())
      .collect(Collectors.joining(", "));
  }

  public String slotNames(Set<EventSlot> slots, Language language) {
    if (slots == null || slots.isEmpty()) {
      return "";
    }

    // Convert the Set to a List
    return slotNames(slots.stream().collect(Collectors.toList()), language);
  }

  public void save (EventSlot eventSlot) {
    this.eventSlotRepo.save(eventSlot);
  }
}
