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
        ;

//    Food.schema().forEach(
//      stringStringMap -> {
//        String eventPartKey = stringStringMap.get("eventPartKey");
//        String type = stringStringMap.get("type");
//        Field field;
//
//        try {
//          switch(type) {
//            case "label":
//              if (request.get("label") != null && request.get("label").isJsonArray()) {
//                String outTextKey = outTextService.updateLabelArray(request.get("label").getAsJsonArray(), request.get(eventPartKey).getAsString());
//                field =  getField(eventPartKey);
//                field.set(food, outTextKey);
//              }
//
//              break;
//            case "text":
//              field = getField(eventPartKey);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? "" : request.get(eventPartKey).getAsString());
//              break;
//
//            case "number":
//              field = getField(eventPartKey);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null : Integer.parseInt(request.get(eventPartKey).getAsString()));
//              break;
//
//            case "double":
//              field = getField(eventPartKey);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null : Double.parseDouble(request.get(eventPartKey).getAsString()));
//              break;
//
//            case "color":
//              field = getField(eventPartKey);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null :
//                request.get(eventPartKey).getAsJsonObject().isJsonNull() ? request.get(eventPartKey).getAsString() :
//                  request.get(eventPartKey).getAsJsonObject().get("hex").getAsString());
//              break;
//
//            case "date":
//              field = getField(eventPartKey);
//
//              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
//              String dateString = request.get(eventPartKey).getAsString();
//
//              // Parse the string into a LocalDate object
//              // hack hack hack, need fix
//              LocalDate localDate = LocalDate.parse(dateString.substring(0, 10), dateFormatter).plusDays(1);
//
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null : localDate);
//              break;
//
//            case "datetime":
//              field = getField(eventPartKey);
//              // registrationStart":{"date":"2023-07-29","time":"23:00"}
//              // Assuming request.get(eventPartKey).getAsString() retrieves the date string
//              String dateTimeDateString = request.get(eventPartKey).getAsJsonObject().get("date").getAsString().substring(0, 10);
//              String dateTimeTimeString = request.get(eventPartKey).getAsJsonObject().get("time").isJsonNull() ? "12:00" : request.get(eventPartKey).getAsJsonObject().get("time").getAsString();
//              ZoneId zoneId = ZoneId.of("Europe/Zurich");
//              LocalDateTime dateTime = LocalDateTime.parse(dateTimeDateString + " " + dateTimeTimeString, dateTimeFormatter);
//
//              // hack hack hack, need fix plus Days
//              ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId).plusDays(1);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null : zonedDateTime);
//              break;
//
//            case "bool":
//              field = getField(eventPartKey);
//              field.set(food, request.get(eventPartKey).isJsonNull() ? null : Boolean.valueOf(request.get(eventPartKey).getAsString()));
//              break;
//
//            default:
//              // Nothing will happen here
//          }
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    );
//    save(food);
//    Slot slot = slotService.findBySlotId(request.get("slot").getAsLong());
////    slotService.updateTypeSlot("food", slot, food.getFoodId());
//    TypeSlot typeSlot = slotService.findByTypeObjectIdAndSlot("food", food.getFoodId(), slot);
//    eventTypeSlotService.updateEventTypeSlot(typeSlot, event, 1);
  }

  public void deleteByEventAndType(Event event, String type) {
    findByEventAndType(event, type).forEach(this::delete);
  }

  @Transactional
  public void delete(EventTypeSlot eventTypeSlot) {
    eventTypeSlotRepo.delete(eventTypeSlot);
  }
}
