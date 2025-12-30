package ch.redanz.redanzCore.model.workshop.service;

import ch.redanz.redanzCore.model.workshop.entities.*;
import ch.redanz.redanzCore.model.workshop.repository.EventTypeSlotRepo;
import com.google.gson.JsonObject;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EventTypeSlotService {
  private final EventTypeSlotRepo eventTypeSlotRepo;
  private final SlotService slotService;

  public void updateEventTypeSlot(TypeSlot typeSlot, Event event, Integer seqNr) {
    if (!eventTypeSlotRepo.existsByEventAndTypeSlot(event, typeSlot)) {
      eventTypeSlotRepo.save(
        new EventTypeSlot(
          typeSlot,
          event,
          seqNr
        )
      );
    }
  }
  public EventTypeSlot findByEventAndTypeSlot(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.findAllByEventAndTypeSlot(event, typeSlot).get();
  }

  public boolean existsByEventAndTypeSlot(Event event, TypeSlot typeSlot) {
    return eventTypeSlotRepo.existsByEventAndTypeSlot(event, typeSlot);
  }

  public List<EventTypeSlot> findByEventAndType(Event event, String type) {
    return eventTypeSlotRepo.findAllByEvent(event)
      .stream()
      .filter(slot -> slot.getTypeSlot().getType().equals(type))
      .collect(Collectors.toList());
  }

  public void updateEventFoodSlot(JsonObject request, Event event) throws IOException, TemplateException {
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    TypeSlot typeSlot;
    EventTypeSlot eventTypeSlot;

    // log.info("inc@updateFood");
    // log.info(request.toString());

    Long foodId = request.get("food").isJsonNull() ? null : request.get("food").getAsLong();
    Long slotId = request.get("slot").isJsonNull() ? null : request.get("slot").getAsLong();
    Integer seqNr = request.get("seqNr").isJsonNull() ? null : request.get("seqNr").getAsInt();
    Long typeSlotId = request.get("id").isJsonNull() ? null : request.get("id").getAsLong();
    boolean typeSlotIsNew;

    if (slotService.typeSlotExists("food", foodId, slotService.findBySlotId(slotId))) {
      typeSlot = slotService.findByTypeObjectIdAndSlot("food", foodId, slotService.findBySlotId(slotId));
    } else {
      typeSlot = new TypeSlot(
        "food", slotService.findBySlotId(slotId), foodId
      );
      slotService.save(typeSlot);
    }

    if (!existsByEventAndTypeSlot(event, typeSlot)) {
      eventTypeSlot = new EventTypeSlot(typeSlot, event, seqNr);
      slotService.save(eventTypeSlot);
    }
    else {
      eventTypeSlot = findByEventAndTypeSlot(event, typeSlot);

      if (eventTypeSlot.getSeqNr() != seqNr) {
        eventTypeSlot.setSeqNr(seqNr);
        slotService.save(eventTypeSlot);
      }
    }
  }

  public void deleteByEventAndType(Event event, String type) {
    findByEventAndType(event, type).forEach(this::delete);
  }

  @Transactional
  public void delete(EventTypeSlot eventTypeSlot) {
    eventTypeSlotRepo.delete(eventTypeSlot);
  }
}
